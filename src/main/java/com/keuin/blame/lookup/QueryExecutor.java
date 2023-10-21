package com.keuin.blame.lookup;

import com.clickhouse.client.ClickHouseParameterizedQuery;
import com.clickhouse.data.ClickHouseFormat;
import com.clickhouse.data.value.ClickHouseLongValue;
import com.clickhouse.data.value.ClickHouseStringValue;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.util.DatabaseUtil;
import com.keuin.blame.util.TablePrinter;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

public class QueryExecutor {

    private final Logger logger = LogManager.getLogger();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void byBlockPos(String world, long x, long y, long z, Consumer<Text> callback) {
        var server = DatabaseUtil.getServer();
        var stmt = ClickHouseParameterizedQuery.of(server.getConfig(),
                "select subject_id, object_id, action_type, ts " +
                        "from :table where subject_world=:world and object_x=:x and object_y=:y and object_z=:z");
        try (var client = DatabaseUtil.getClient(server)) {
            var resp = client.read(server)
                    .format(ClickHouseFormat.RowBinaryWithNamesAndTypes)
                    .query(stmt)
                    .params(
                            ClickHouseStringValue.of(DatabaseUtil.DB_CONFIG.table()),
                            ClickHouseStringValue.of(world),
                            ClickHouseLongValue.of(x),
                            ClickHouseLongValue.of(y),
                            ClickHouseLongValue.of(z)
                    )
                    .execute()
                    .get();
            int cnt = 0;
            var sb = new StringBuilder();
            sb.append(String.format("Result for block at %s (%d, %d, %d):\n", world, x, y, z));
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
            sb.append(table);
            sb.append(String.format("%d records in total.", cnt));
            callback.accept(new LiteralText(sb.toString()));
        } catch (Exception ex) {
            logger.error("Query block failed", ex);
            callback.accept(new LiteralText("Query failed: " + ex));
        }
    }
}
