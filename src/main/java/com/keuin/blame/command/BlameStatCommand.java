package com.keuin.blame.command;

import com.google.common.base.Strings;
import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.data.helper.VersionedLogEntryHelper;
import com.keuin.blame.util.DatabaseUtil;
import com.keuin.blame.util.PrintUtil;
import com.mojang.brigadier.context.CommandContext;
import com.mongodb.MongoClientException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

public class BlameStatCommand {

    private static final Logger logger = Logger.getLogger(BlameStatCommand.class.getName());

    public static int showStat(CommandContext<ServerCommandSource> context) {
        PrintUtil.message(context, Formatting.ITALIC, "Collecting statistics. This may take a few seconds...");
        showStat(stat -> {
            PrintUtil.Printer sb = PrintUtil.newPrinter();
            if (stat != null) {
                sb.append("Logs grouped by subjects:").newline();
                boolean isFirst = true;
                for (Map.Entry<String, Long> entry : stat.getCountMap().entrySet()) {
                    if (!isFirst)
                        sb.newline();
                    isFirst = false;
                    final String subjectId = entry.getKey();
                    final long count = entry.getValue();
                    sb.append(Formatting.YELLOW, Optional.ofNullable(Strings.emptyToNull(subjectId)).orElse("null"))
                            .append(": ").append(count);
                }
            } else {
                sb.append(Formatting.RED, "Failed to get statistics. Please refer to server log for more information.");
            }
            sb.sendTo(context);
        });
        return Commands.SUCCESS;
    }

    public static void showStat(ShowStatCallback callback) {
        Objects.requireNonNull(callback);
        new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("Collecting statistics...");
                try (final MongoClient mongoClient = MongoClients.create(DatabaseUtil.CLIENT_SETTINGS)) {
                    final MongoDatabase db = mongoClient.getDatabase(
                            DatabaseUtil.MONGO_CONFIG.getDatabaseName()
                    );
                    final MongoCollection<LogEntry> collection = db.getCollection(
                            DatabaseUtil.MONGO_CONFIG.getLogCollectionName(), LogEntry.class
                    );
                    Collection<String> ids = VersionedLogEntryHelper.getLoggedSubjectsId(collection);

                    // count by distinct subjects
                    Map<String, Long> countMap = new HashMap<>();
                    for (String subjectId : ids) {
                        long count = VersionedLogEntryHelper.countBySubjectId(collection, subjectId);
                        countMap.put(subjectId, count);
                    }

                    // invoke callback
                    callback.showStat(new BlameStat(countMap));
                } catch (MongoClientException exception) {
                    logger.severe("Failed when querying the database: " + exception +
                            ". Failed to get statistics.");
                    callback.showStat(null);
                }
            }
        }, "BlameStatCommandThread").start();
    }

    public static class BlameStat {
        private final Map<String, Long> countMap;

        public BlameStat(Map<String, Long> countMap) {
            this.countMap = countMap;
        }

        public Map<String, Long> getCountMap() {
            return Collections.unmodifiableMap(countMap);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlameStat blameStat = (BlameStat) o;
            return Objects.equals(countMap, blameStat.countMap);
        }

        @Override
        public int hashCode() {
            return Objects.hash(countMap);
        }
    }

    public interface ShowStatCallback {
        void showStat(@Nullable BlameStat stat);
    }

}
