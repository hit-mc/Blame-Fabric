package com.keuin.blame.command;

import com.keuin.blame.data.WorldPos;
import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.lookup.BlockPosLookupFilter;
import com.keuin.blame.lookup.LookupCallback;
import com.keuin.blame.lookup.LookupManager;
import com.keuin.blame.util.PrintUtil;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
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
            StringBuilder printBuilder = new StringBuilder();
            int printCount = 0;
            for (LogEntry logEntry : logEntries) {
                printBuilder.append(logEntry.toString());
                printBuilder.append("\n")
                        .append("================")
                        .append("\n");
                ++printCount;
            }
            printBuilder.append(String.format("Displayed the most recent %d items. " +
                    "Use `/blame limit` to change your display limit.", printCount));
            PrintUtil.msgInfo(context, printBuilder.toString());
        }
    }

}
