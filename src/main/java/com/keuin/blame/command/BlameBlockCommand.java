package com.keuin.blame.command;

import com.keuin.blame.data.LogEntry;
import com.keuin.blame.data.WorldPos;
import com.keuin.blame.lookup.BlockPosLookupFilter;
import com.keuin.blame.lookup.LookupCallback;
import com.keuin.blame.lookup.LookupManager;
import com.keuin.blame.util.PrintUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Iterator;

public class BlameBlockCommand {

    private static final int SUCCESS = 1;
    private static final int FAILED = -1;

    public static int run(CommandContext<ServerCommandSource> context) {
        Entity entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayerEntity)) {
            // can only be executed by player
            return FAILED;
        }

        ServerPlayerEntity playerEntity = (ServerPlayerEntity) entity;
        int x = context.getArgument("x", Integer.class);
        int y = context.getArgument("y", Integer.class);
        int z = context.getArgument("z", Integer.class);
        String world = context.getArgument("world", String.class);
//        String world = MinecraftUtil.worldToString(playerEntity.world);
        WorldPos blockPos = new WorldPos(world, x, y, z);
        LookupManager.INSTANCE.lookup(
                new BlockPosLookupFilter(blockPos),
                new Callback(context)
        );
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
            Iterator<LogEntry> iterator = logEntries.iterator();
            int printCount;
            for (printCount = 0; printCount < 5; ++printCount) {
                if (!iterator.hasNext())
                    break;
                LogEntry logEntry = iterator.next();
                printBuilder.append(logEntry.toString());
                printBuilder.append("\n")
                        .append("================")
                        .append("\n");
            }
            printBuilder.append(String.format("Displayed the most recent %d items.", printCount));
            PrintUtil.msgInfo(context, printBuilder.toString());
        }
    }
}
