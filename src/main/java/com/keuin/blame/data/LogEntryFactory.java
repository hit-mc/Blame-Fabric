package com.keuin.blame.data;

import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.data.enums.ObjectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class LogEntryFactory {
    public static LogEntry playerWithBlock(PlayerEntity player, String playerWorld, String blockId, Vec3i blockPos, String blockWorld, ActionType actionType) {
        Vec3d playerPos = player.getPos();
        return new LogEntry(
                System.currentTimeMillis(),
                player.getName().asString(),
                player.getUuid(),
                new WorldPos(playerWorld, playerPos.x, playerPos.y, playerPos.z),
                actionType,
                ObjectType.BLOCK,
                blockId,
                new WorldPos(blockWorld, blockPos.getX(), blockPos.getY(), blockPos.getZ())
        );
    }

    public static LogEntry playerWithEntity(PlayerEntity player, String playerWorld, String entityId, Vec3d entityPos, String entityWorld, ActionType actionType) {
        Vec3d playerPos = player.getPos();
        return new LogEntry(
                System.currentTimeMillis(),
                player.getName().asString(),
                player.getUuid(),
                new WorldPos(playerWorld, playerPos.x, playerPos.y, playerPos.z),
                actionType,
                ObjectType.ENTITY,
                entityId,
                new WorldPos(entityWorld, entityPos.x, entityPos.y, entityPos.z)
        );
    }

    public static LogEntry playerWithItem(PlayerEntity player, String playerWorld, String itemId, ActionType actionType) {
        Vec3d playerPos = player.getPos();
        return new LogEntry(
                System.currentTimeMillis(),
                player.getName().asString(),
                player.getUuid(),
                new WorldPos(playerWorld, playerPos.x, playerPos.y, playerPos.z),
                actionType,
                ObjectType.ENTITY,
                itemId,
                WorldPos.NULL_POS
        );
    }
}
