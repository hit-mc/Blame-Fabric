package com.keuin.blame.command;

import com.keuin.blame.data.WorldPos;
import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.lookup.BlockPosLookupFilter;
import com.keuin.blame.lookup.LookupCallback;
import com.keuin.blame.lookup.LookupManager;
import com.keuin.blame.util.PrettyUtil;
import com.keuin.blame.util.PrintUtil;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.keuin.blame.command.Commands.FAILED;
import static com.keuin.blame.command.Commands.SUCCESS;

@SuppressWarnings("SameReturnValue")
public class BlameBlockCommand {

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
        Identifier world = context.getArgument("world", Identifier.class);
//        String world = MinecraftUtil.worldToString(playerEntity.world);
        WorldPos blockPos = new WorldPos(world.toString(), x, y, z);
        LookupManager.INSTANCE.lookup(
                new BlockPosLookupFilter(blockPos),
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
