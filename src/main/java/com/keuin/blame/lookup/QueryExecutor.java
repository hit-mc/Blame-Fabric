package com.keuin.blame.lookup;

import com.clickhouse.data.ClickHouseFormat;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.util.DatabaseUtil;
import com.keuin.blame.util.TablePrinter;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

public class QueryExecutor {

    private final Logger logger = LogManager.getLogger(QueryExecutor.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String getSQL(
            String world,
            long x, long y, long z,
            long timeRange,
            int maxCount
    ) {
        // ClickHouse driver's parameterized SQL generator is a piece of shit.
        // I won't use that. Use string interpolation instead.
        var sql = "select subject_id, object_id, action_type, ts";
        sql += " from " + escapeIdentifier(DatabaseUtil.DB_CONFIG.getTable());
        sql += " where subject_world=%s and object_x=%d and object_y=%d and object_z=%d".formatted(
                escape(world), x, y, z
        );
        if (timeRange > 0) {
            sql += " and ts>=" + timeRange;
        }
        if (maxCount > 0) {
            sql += " limit " + maxCount;
        }
        return sql;
    }

    private static String escape(String s) {
        return "'" + s.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }

    private static String escapeIdentifier(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\"\"") + "\"";
    }

    public void byBlockPos(
            String world,
            long x, long y, long z,
            long timeRange,
            int maxCount,
            Consumer<Text> callback
    ) {
        var server = DatabaseUtil.getServer();
        var sql = getSQL(world, x, y, z, timeRange, maxCount);
        logger.info("SQL: " + sql);
        try (var client = DatabaseUtil.getClient(server)) {
            var resp = client.read(server)
                    .format(ClickHouseFormat.RowBinaryWithNamesAndTypes)
                    .query(sql)
                    .execute()
                    .get();
            int cnt = 0;
            MutableText t = new LiteralText("");
            t = t.append(String.format("Result for block at %s (%d, %d, %d):\n", world, x, y, z));
            final int columns = 4;
            var table = new TablePrinter(columns);
            table.add(new TablePrinter.Row("Player", "Object", "Action", "Time"));
            for (var row : resp.records()) {
                var player = row.getValue("subject_id").asString();
                var obj = row.getValue("object_id").asString().replaceFirst("^minecraft:", "");
                var actionType = Optional.
                        ofNullable(ActionType.parseInt(row.getValue("action_type").asInteger())).
                        map(ActionType::toString).orElse("/");
                var time = sdf.format(new Date(row.getValue("ts").asLong()));
                table.add(new TablePrinter.Row(player, obj, actionType, time));
                cnt++;
            }
            t = t.append(table.build());
            t = t.append(String.format("%d records in total.", cnt));
            callback.accept(t);
        } catch (Exception ex) {
            logger.error("Query block failed", ex);
            callback.accept(new LiteralText("Query failed: " + ex));
        }
    }
}
