package com.keuin.blame.command;

import com.keuin.blame.Blame;
import com.keuin.blame.util.MinecraftUtil;
import com.keuin.blame.util.PrintUtil;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Set;

import static com.keuin.blame.command.Commands.FAILED;
import static com.keuin.blame.command.Commands.SUCCESS;

@SuppressWarnings("SameReturnValue")
public class BlameBlockCommand {

    public static final Map<String, Integer> timeUnitAmplifierMap = Map.of(
            "second", 1,
            "minute", 60,
            "hour", 3600,
            "day", 86400
    );
    public static final Set<String> timeUnits = timeUnitAmplifierMap.keySet();

    public static int blameGivenBlockPos(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // pos
        // world
        Entity entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayerEntity)) {
            // can only be executed by player
            PrintUtil.error(context, "This command cannot be used in server console.");
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
            if (timeRange < 0) {
                PrintUtil.error(context, "Time range must be positive.");
                return FAILED;
            }
        } catch (IllegalArgumentException e) {
            timeRange = -1;
        }

        long amplifier;
        try {
            final String timeUnit = StringArgumentType.getString(context, "time_unit");
            amplifier = timeUnitAmplifierMap.getOrDefault(timeUnit, -1);
            if (amplifier < 0) {
                PrintUtil.error(context, "Invalid time unit.");
                return FAILED;
            }
        } catch (IllegalArgumentException e) {
            amplifier = 1;
        }

        if (timeRange >= 0) {
            timeRange *= amplifier;
            if (timeRange < 0)
                return FAILED;
            // convert to maximum unix millis
            timeRange = System.currentTimeMillis() - timeRange * 1000L;
        }

        int amountLimit;
        try {
            amountLimit = IntegerArgumentType.getInteger(context, "amount");
            if (amountLimit <= 0) {
                PrintUtil.error(context, "Amount must be positive.");
                return FAILED;
            }
        } catch (IllegalArgumentException e) {
            amountLimit = BlameLimitCommand.DEFAULT_LOOKUP_LIMIT;
        }

        Blame.queryExecutor.byBlockPos(world, x, y, z, timeRange, amountLimit, (msg) ->
                context.getSource().sendFeedback(msg, false));

        return SUCCESS;
    }

    // TODO
    public static int blameGivenBlockRange(CommandContext<ServerCommandSource> context) {
        return SUCCESS;
    }

}
