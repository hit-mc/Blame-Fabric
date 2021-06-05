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
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.logging.Logger;

public class Blame implements ModInitializer {

    private static final Logger logger = Logger.getLogger(Blame.class.getName());

    public static BlameConfig config;

    public static boolean loadConfig() {
        String configFileName = "blame.json";
        try {
            // load config
            File configFile = new File(configFileName);
            if (!configFile.exists()) {
                logger.severe(String.format("Configuration file %s does not exist. " +
                        "Blame will be disabled.", configFileName));
                return false;
            }

            Reader reader = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8);
            config = (new Gson()).fromJson(reader, BlameConfig.class);
        } catch (IOException exception) {
            logger.severe(String.format("Failed to read configuration file %s: %s. " +
                    "Blame will be disabled.", configFileName, exception.getMessage()));
            return false;
        }
        return true;
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
        ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
            logger.info("Stopping LookupManager...");
            LookupManager.INSTANCE.stop();

            logger.info("Stopping SubmitWorker...");
            SubmitWorker.INSTANCE.stop();
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
        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) -> {
            final LiteralArgumentBuilder<ServerCommandSource> amountLimitArgument = CommandManager.literal("limit")
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                            .suggests((ctx, builder) -> CommandSource.suggestMatching(Arrays.asList("1", "5", "10"), builder))
                            .executes(BlameBlockCommand::blameGivenBlockPos)
                    ).executes(BlameBlockCommand::blameGivenBlockPos);
            final LiteralArgumentBuilder<ServerCommandSource> timeRangeArgument = CommandManager.literal("last")
                    .then(CommandManager.argument("time_range", LongArgumentType.longArg())
                            .executes(BlameBlockCommand::blameGivenBlockPos)
                            .then(CommandManager.argument("time_unit", StringArgumentType.word())
                                    .suggests((ctx, builder) -> CommandSource.suggestMatching(BlameBlockCommand.timeUnits, builder))
                                    .executes(BlameBlockCommand::blameGivenBlockPos)
                                    .then(amountLimitArgument)
                            )

                    );
            commandDispatcher.register(
                    CommandManager.literal("blame").then(CommandManager.literal("block")
                            .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                    .executes(BlameBlockCommand::blameGivenBlockPos)
                                    .then(CommandManager.argument("world", DimensionArgumentType.dimension())
                                            .executes(BlameBlockCommand::blameGivenBlockPos)
                                            .then(timeRangeArgument)
                                            .then(amountLimitArgument)
                                    )))
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
        });
    }
}
