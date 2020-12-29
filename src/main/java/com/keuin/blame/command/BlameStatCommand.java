package com.keuin.blame.command;

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
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

public class BlameStatCommand {

    private static final Logger logger = Logger.getLogger(BlameStatCommand.class.getName());

    public static int showStat(CommandContext<ServerCommandSource> context) {
        showStat(new ShowStatCallback() {
            @Override
            public void showStat(@Nullable BlameStat stat) {
                StringBuilder sb = new StringBuilder();
                if (stat != null) {
                    sb.append("Data statistics\n");
                    sb.append("===============\n");
                    sb.append("\n");
                    sb.append("# Count by subjects\n");
                    stat.getCountMap().forEach((subjectId, count) -> {
                        sb.append(subjectId).append(": ").append(count).append("\n");
                    });
                    sb.append("\n");
                    sb.append("=== END ===\n");
                } else {
                    sb.append("Failed to get statistics. Please refer to server log for more information.\n");
                }
                PrintUtil.msgInfo(context, sb.toString());
            }
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
        }).start();
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
