package com.keuin.blame;

import com.google.gson.Gson;
import com.keuin.blame.adapter.*;
import com.keuin.blame.adapter.handler.PlaceBlockHandler;
import com.keuin.blame.command.BlameBlockCommand;
import com.keuin.blame.command.BlameLimitCommand;
import com.keuin.blame.command.BlameStatCommand;
import com.keuin.blame.config.BlameConfig;
import com.keuin.blame.lookup.LookupManager;
import com.keuin.blame.util.DatabaseUtil;
import com.keuin.blame.util.PrintUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Blame implements ModInitializer {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Blame.class.getName());

    public static BlameConfig config;

    public static boolean loadConfig() {
        String configFileName = "blame.json";
        try {
            // load config
            File configFile = new File(configFileName);
            if (!configFile.exists()) {
                logger.severe(String.format("Failed to read configuration file %s. Blame will be disabled.", configFileName));
                return false;
            }

            Reader reader = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8);
            config = (new Gson()).fromJson(reader, BlameConfig.class);
        } catch (IOException exception) {
            logger.severe(String.format("Failed to read configuration file %s: %s. " +
                    "Blame will be disabled.", configFileName, exception));
            return false;
        }
        return true;
    }



    private static void upgradeOldLogEntries() {
//        try (final MongoClient mongoClient = MongoClients.create(DatabaseUtil.CLIENT_SETTINGS)) {
//            final MongoDatabase db = mongoClient.getDatabase(
//                    DatabaseUtil.MONGO_CONFIG.getDatabaseName()
//            );
//            final MongoCollection<LogEntry> collection = db.getCollection(
//                    DatabaseUtil.MONGO_CONFIG.getLogCollectionName(), LogEntry.class
//            );
//            collection.updateMany()
//            FindIterable<LogEntry> iterable =
//                    collection.find(Filters.ne(LogEntryNames.VERSION, TransformerManager.LATEST_VERSION))
//                    .showRecordId(true);
//            for (LogEntry logEntry : iterable) {
//                if (logEntry.version > TransformerManager.LATEST_VERSION) {
//                    logger.warning("Detected a newer entry in the database! " +
//                            "Downgrading of Blame is not supported and may cause " +
//                            "unexpected behaviour.");
//                    continue;
//                }
//
//                collection.updateOne(iterable.showRecordId())
//            }
//
//        }
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        if (!loadConfig())
            return;

        DatabaseUtil.disableMongoSpamming();

        // hook disable event
        ServerLifecycleEvents.SERVER_STOPPING.register(new ServerLifecycleEvents.ServerStopping() {
            @Override
            public void onServerStopping(MinecraftServer minecraftServer) {
                logger.info("Stopping LookupManager...");
                LookupManager.INSTANCE.stop();

                logger.info("Stopping SubmitWorker...");
                SubmitWorker.INSTANCE.stop();
            }
        });

        // hook game events
        AttackEntityCallback.EVENT.register(new AttackEntityAdapter(EventHandler.INSTANCE));
        PlaceBlockHandler.EVENT.register(EventHandler.INSTANCE);
        PlayerBlockBreakEvents.AFTER.register(new BreakBlockAdapter(EventHandler.INSTANCE));
        UseBlockCallback.EVENT.register(new UseBlockAdapter(EventHandler.INSTANCE));
        UseEntityCallback.EVENT.register(new UseEntityAdapter(EventHandler.INSTANCE));
        UseItemCallback.EVENT.register(new UseItemAdapter(EventHandler.INSTANCE));

        // initialize PrintUtil
        ServerLifecycleEvents.SERVER_STARTED.register(PrintUtil.INSTANCE);

        // register
        CommandRegistrationCallback.EVENT.register(new CommandRegistrationCallback() {
            @Override
            public void register(CommandDispatcher<ServerCommandSource> commandDispatcher, boolean b) {
                commandDispatcher.register(
                        CommandManager.literal("blame").then(CommandManager.literal("block")
                                .then(CommandManager.argument("x", IntegerArgumentType.integer())
                                        .then(CommandManager.argument("y", IntegerArgumentType.integer())
                                                .then(CommandManager.argument("z", IntegerArgumentType.integer())
                                                        .then(CommandManager.argument("world", StringArgumentType.greedyString())
                                                                .executes(BlameBlockCommand::blameGivenBlockPos))))))
                );
                commandDispatcher.register(
                        CommandManager.literal("blame").then(CommandManager.literal("limit")
                                .then(CommandManager.argument("limit", IntegerArgumentType.integer(1, 255))
                                        .executes(BlameLimitCommand::setLimit)))
                );
                commandDispatcher.register(
                        CommandManager.literal("blame").then(CommandManager.literal("stat")
                        .executes(BlameStatCommand::showStat))
                );
            }
        });
    }
}
