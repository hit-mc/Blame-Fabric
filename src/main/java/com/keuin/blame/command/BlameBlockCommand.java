package com.keuin.blame.command;

import com.keuin.blame.data.WorldPos;
import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.lookup.*;
import com.keuin.blame.util.MinecraftUtil;
import com.keuin.blame.util.PrettyUtil;
import com.keuin.blame.util.PrintUtil;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.keuin.blame.command.Commands.FAILED;
import static com.keuin.blame.command.Commands.SUCCESS;

@SuppressWarnings("SameReturnValue")
public class BlameBlockCommand {

    public static final Map<String, Integer> timeUnitAmplifierMap = Collections
            .unmodifiableMap(new HashMap<String, Integer>() {{
                put("second", 1);
                put("minute", 60);
                put("hour", 3600);
                put("day", 86400);
            }});
    public static final Set<String> timeUnits = timeUnitAmplifierMap.keySet();

    public static int blameGivenBlockPos(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // pos
        // world
        Entity entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayerEntity)) {
            // can only be executed by player
            return FAILED;
        }

        ServerPlayerEntity playerEntity = (ServerPlayerEntity) entity;
        BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(context, "pos");
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        String world;
        try {
            world = context.getArgument("world", Identifier.class).toString();
        } catch (IllegalArgumentException e) {
            world = MinecraftUtil.worldToString(entity.world);
        }

        long timeRange;
        try {
            timeRange = LongArgumentType.getLong(context, "time_range");
            if (timeRange < 0)
                return FAILED;
        } catch (IllegalArgumentException e) {
            timeRange = -1;
        }

        long amplifier;
        try {
            final String timeUnit = StringArgumentType.getString(context, "time_unit");
            amplifier = timeUnitAmplifierMap.getOrDefault(timeUnit, -1);
            if (amplifier < 0)
                return FAILED;
        } catch (IllegalArgumentException e) {
            amplifier = 1;
        }

        if (timeRange >= 0) {
            timeRange *= amplifier;
            if (timeRange < 0)
                return FAILED;
        }

//        String world = MinecraftUtil.worldToString(playerEntity.world);
        WorldPos blockPos = new WorldPos(world, x, y, z);
        AbstractLookupFilter filter;
        if (timeRange >= 0) {
            filter = LookupFilters.compoundedFilter(new TimeLookupFilter(timeRange), new BlockPosLookupFilter(blockPos));
        } else {
            filter = new BlockPosLookupFilter(blockPos);
        }

        LookupManager.INSTANCE.lookup(
                filter,
                new Callback(context),
                BlameLimitCommand.getLookupLimit(playerEntity.getUuid())
        );

        return SUCCESS;
    }

    // TODO
    public static int blameGivenBlockRange(CommandContext<ServerCommandSource> context) {
        return SUCCESS;
    }

    private static class Callback implements LookupCallback {

        private final CommandContext<ServerCommandSource> context;

        private Callback(CommandContext<ServerCommandSource> context) {
            this.context = context;
        }

        @Override
        public void onLookupFinishes(Iterable<LogEntry> logEntries) {
            int printCount = 0;
            PrintUtil.Printer printer = PrintUtil.newPrinter();
            boolean isFirst = true;
            for (LogEntry logEntry : logEntries) {
                if (!isFirst)
                    printer.append("\n");
                printer.append(Formatting.YELLOW, "Time: ", PrettyUtil.timestampToString(logEntry.timeMillis), "\n")
                        .append(Formatting.YELLOW, "Subject: ", Formatting.AQUA, logEntry.subjectId, "{", logEntry.subjectUUID, "} @ ", logEntry.subjectPos, "\n")
                        .append(Formatting.YELLOW, "Action: ", Formatting.AQUA, logEntry.actionType, "\n")
                        .append(Formatting.YELLOW, "Object: ", Formatting.AQUA, logEntry.objectType, "[", logEntry.objectId, "] @ ", logEntry.objectPos, "\n")
                        .append(Formatting.YELLOW, "Log version: ", logEntry.version, "\n")
                        .append(Formatting.YELLOW, "Game version: ", logEntry.gameVersion, "\n")
                        .append("================");
                ++printCount;
                isFirst = false;
            }
            if (printCount > 0) {
                printer.sendTo(context);
                PrintUtil.message(context,
                        "Showed " + printCount + " event items. ",
                        Formatting.ITALIC,
                        "Use `/blame limit` to change print count limit.");
            } else {
                PrintUtil.message(context, "No logs found.");
            }
        }
    }

}
