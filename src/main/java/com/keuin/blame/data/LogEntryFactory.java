package com.keuin.blame.data;

import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.data.enums.ObjectType;
import com.keuin.blame.util.MinecraftUtil;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

public class LogEntryFactory {
    public static LogEntry entityWithBlock(LivingEntity livingEntity, String playerWorld, String blockId, Vec3i blockPos, String blockWorld, ActionType actionType) {
        Vec3d playerPos = livingEntity.getPos();
        return new LogEntry(
                System.currentTimeMillis(),
                livingEntity.getName().asString(),
                livingEntity.getUuid(),
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


    @SuppressWarnings("ConstantConditions")
    public static LogEntry endGatewayTeleported(Entity entity, EndGatewayBlockEntity gatewayBlockEntity) {
        var world = gatewayBlockEntity.hasWorld() ? gatewayBlockEntity.getWorld() : Objects.requireNonNull(entity.world);
        var worldName = MinecraftUtil.worldToString(world);
        var gatewayPos = gatewayBlockEntity.getPos();
        var itemId = Registry.ITEM.getId(gatewayBlockEntity.getCachedState().getBlock().asItem()).toString();
        return new LogEntry(
                System.currentTimeMillis(),
                entity.getName().asString(),
                entity.getUuid(),
                new WorldPos(worldName, entity.getX(), entity.getY(), entity.getZ()),
                ActionType.TELEPORTED_BY,
                ObjectType.BLOCK,
                itemId,
                new WorldPos(worldName, gatewayPos.getX(), gatewayPos.getY(), gatewayPos.getZ())
        );
    }
}
