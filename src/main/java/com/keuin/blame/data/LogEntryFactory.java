package com.keuin.blame.data;

import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.data.enums.ObjectType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.UUID;

public class LogEntryFactory {
    public static LogEntry playerWithBlock(UUID playerUUID, Vec3d playerPos, String playerWorld, String blockId, Vec3i blockPos, String blockWorld, ActionType actionType) {
        return new LogEntry(
                System.currentTimeMillis(),
                playerUUID,
                new WorldPos(playerWorld, playerPos.x, playerPos.y, playerPos.z),
                actionType,
                ObjectType.BLOCK,
                blockId,
                new WorldPos(blockWorld, blockPos.getX(), blockPos.getY(), blockPos.getZ())
        );
    }

    public static LogEntry playerWithEntity(UUID playerUUID, Vec3d playerPos, String playerWorld, String entityId, Vec3d entityPos, String entityWorld, ActionType actionType) {
        return new LogEntry(
                System.currentTimeMillis(),
                playerUUID,
                new WorldPos(playerWorld, playerPos.x, playerPos.y, playerPos.z),
                actionType,
                ObjectType.ENTITY,
                entityId,
                new WorldPos(entityWorld, entityPos.x, entityPos.y, entityPos.z)
        );
    }

    public static LogEntry playerWithItem(UUID playerUUID, Vec3d playerPos, String playerWorld, String itemId, ActionType actionType) {
        return new LogEntry(
                System.currentTimeMillis(),
                playerUUID,
                new WorldPos(playerWorld, playerPos.x, playerPos.y, playerPos.z),
                actionType,
                ObjectType.ENTITY,
                itemId,
                WorldPos.NULL_POS
        );
    }
}
