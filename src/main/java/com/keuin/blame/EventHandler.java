package com.keuin.blame;

import com.keuin.blame.adapter.handler.*;
import com.keuin.blame.data.LogEntryFactory;
import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.util.MinecraftUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EventHandler implements AttackEntityHandler, PlaceBlockHandler, BreakBlockHandler, UseBlockHandler, UseEntityHandler, UseItemHandler {

    public static final EventHandler INSTANCE = new EventHandler();

    private static final long MAX_DUPE_INTERVAL = 100; // （判定为重复的必要条件）最大间隔毫秒数

    private LogEntry lastUseBlockEntry; //去重用
    private LogEntry lastUseEntityEntry;
    private LogEntry lastUseItemEntry; // TODO

    private EventHandler() {
    }

    @Override
    public void onPlayerUseBlock(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        boolean checkDuplication = false;
        long ts = System.currentTimeMillis();
        if (lastUseBlockEntry != null && (ts - lastUseBlockEntry.timeMillis) <= MAX_DUPE_INTERVAL) {
            if (
                    lastUseBlockEntry.subjectPos.getX() == playerEntity.getX()
                            && lastUseBlockEntry.subjectPos.getY() == playerEntity.getY()
                            && lastUseBlockEntry.subjectPos.getZ() == playerEntity.getZ() // 快速判断，减小不相同导致的资源开销
            ) {
                checkDuplication = true;
            }
        }
        String worldString = MinecraftUtil.worldToString(world);
        String blockId = Registry.BLOCK.getId(world.getBlockState(blockHitResult.getBlockPos()).getBlock()).toString();
        LogEntry entry = LogEntryFactory.playerWithBlock(
                playerEntity,
                worldString,
                blockId,
                blockHitResult.getBlockPos(),
                worldString,
                ActionType.BLOCK_USE
        );
        if (checkDuplication) {
            LogEntry alignedEntry = new LogEntry(lastUseBlockEntry);
            alignedEntry.timeMillis = entry.timeMillis;
            if (Objects.equals(alignedEntry, entry))
                return;
        }
        lastUseBlockEntry = entry;
        SubmitWorker.INSTANCE.submit(entry);
        // TODO: 增加判断，事件触发的时候用户不一定真正使用了方块（也可能是无效的动作）。放置方块的时候也会触发这个事件
    }

    @Override
    public void onPlayerPlaceBlock(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        String blockId = Registry.BLOCK.getId(blockState.getBlock()).toString();
        String worldString = MinecraftUtil.worldToString(world);
        LogEntry entry = LogEntryFactory.playerWithBlock(
                playerEntity,
                worldString,
                blockId,
                blockPos,
                worldString,
                ActionType.BLOCK_PLACE
        );
        SubmitWorker.INSTANCE.submit(entry);
    }

    @Override
    public void onPlayerBreakBlock(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        String worldString = MinecraftUtil.worldToString(world);
        String blockId = Registry.BLOCK.getId(blockState.getBlock()).toString();
        LogEntry entry = LogEntryFactory.playerWithBlock(
                playerEntity,
                worldString,
                blockId,
                blockPos,
                worldString,
                ActionType.BLOCK_BREAK
        );
        SubmitWorker.INSTANCE.submit(entry);
    }

    @Override
    public void onPlayerAttackEntity(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        String entityId = Registry.ENTITY_TYPE.getId(entity.getType()).toString();
        String worldString = MinecraftUtil.worldToString(world);
        LogEntry entry = LogEntryFactory.playerWithEntity(
                playerEntity,
                worldString,
                entityId,
                entity.getPos(),
                worldString,
                ActionType.ENTITY_ATTACK
        );
        SubmitWorker.INSTANCE.submit(entry);
    }

    @Override
    public void onPlayerUseEntity(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        boolean checkDuplication = false;
        long ts = System.currentTimeMillis();
        if (lastUseEntityEntry != null && (ts - lastUseEntityEntry.timeMillis) <= MAX_DUPE_INTERVAL) {
            if (
                    lastUseEntityEntry.subjectPos.getX() == playerEntity.getX()
                            && lastUseEntityEntry.subjectPos.getY() == playerEntity.getY()
                            && lastUseEntityEntry.subjectPos.getZ() == playerEntity.getZ() // 快速判断，减小不相同导致的资源开销
                            && lastUseEntityEntry.objectPos.getX() == entity.getX()
                            && lastUseEntityEntry.objectPos.getY() == entity.getY()
                            && lastUseEntityEntry.objectPos.getZ() == entity.getZ()
            ) {
                checkDuplication = true;
            }
        }
        String entityId = Registry.ENTITY_TYPE.getId(entity.getType()).toString();
        String worldString = MinecraftUtil.worldToString(world);
        LogEntry entry = LogEntryFactory.playerWithEntity(
                playerEntity,
                worldString,
                entityId,
                entity.getPos(),
                worldString,
                ActionType.ENTITY_USE
        );
        if (checkDuplication) {
            LogEntry alignedEntry = new LogEntry(lastUseEntityEntry);
            alignedEntry.timeMillis = entry.timeMillis;
            if (Objects.equals(alignedEntry, entry)) {
                return;
            }
        }
        lastUseEntityEntry = entry;
        SubmitWorker.INSTANCE.submit(entry);
        // TODO: 增加判断，无效的时候也会触发这个事件
    }

    @Override
    public void onPlayerUseItem(PlayerEntity playerEntity, World world, Hand hand) {
        boolean checkDuplication = false;
        long ts = System.currentTimeMillis();
        if (lastUseItemEntry != null && (ts - lastUseItemEntry.timeMillis) <= MAX_DUPE_INTERVAL) {
            if (
                    lastUseItemEntry.subjectPos.getX() == playerEntity.getX()
                            && lastUseItemEntry.subjectPos.getY() == playerEntity.getY()
                            && lastUseItemEntry.subjectPos.getZ() == playerEntity.getZ() // 快速判断，减小不相同导致的资源开销
            ) {
                checkDuplication = true;
            }
        }
        String itemId = Registry.ITEM.getId(playerEntity.getStackInHand(hand).getItem()).toString();
        LogEntry entry = LogEntryFactory.playerWithItem(
                playerEntity,
                MinecraftUtil.worldToString(world),
                itemId,
                ActionType.ITEM_USE
        );
        if (checkDuplication) {
            LogEntry alignedEntry = new LogEntry(lastUseItemEntry);
            alignedEntry.timeMillis = entry.timeMillis;
            if (Objects.equals(alignedEntry, entry)) {
                return;
            }
        }
        lastUseItemEntry = entry;
        SubmitWorker.INSTANCE.submit(entry);
    }

}
