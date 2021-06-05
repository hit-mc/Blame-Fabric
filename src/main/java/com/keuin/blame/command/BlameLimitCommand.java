package com.keuin.blame.command;

import com.keuin.blame.util.PrintUtil;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.keuin.blame.command.Commands.FAILED;
import static com.keuin.blame.command.Commands.SUCCESS;

public class BlameLimitCommand {

    private static final Map<UUID, Integer> maxLookupCount = new ConcurrentHashMap<>();
    public static final int DEFAULT_LOOKUP_LIMIT = 5;

    public static int setLimit(CommandContext<ServerCommandSource> context) {
        Entity entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayerEntity)) {
            // can only be executed by player
            return FAILED;
        }
        ServerPlayerEntity playerEntity = (ServerPlayerEntity) entity;
        int newLimit = context.getArgument("limit", Integer.class);
        int previousLimit = setLookupLimit(playerEntity.getUuid(), newLimit);
        PrintUtil.msgInfo(context, String.format("Set your lookup limit to %d (%d previously).", newLimit, previousLimit));
        return SUCCESS;
    }

    /**
     * Set one player's lookup limit.
     *
     * @param playerUUID     the player's uuid.
     * @param newLookupLimit the new limit. Must be positive.
     * @return the previous (or default if not set) limit.
     */
    private static int setLookupLimit(UUID playerUUID, int newLookupLimit) {
        return Optional.ofNullable(maxLookupCount.put(playerUUID, newLookupLimit)).orElse(DEFAULT_LOOKUP_LIMIT);
    }

    /**
     * Get one player's lookup limit.
     *
     * @param playerUUID the player's uuid.
     * @return the limit.
     */
    @Deprecated
    public static int getLookupLimit(UUID playerUUID) {
        return maxLookupCount.getOrDefault(playerUUID, DEFAULT_LOOKUP_LIMIT);
    }

}
