package com.keuin.blame;

import com.keuin.blame.adapter.handler.*;
import com.keuin.blame.data.LogEntryFactory;
import com.keuin.blame.data.entry.LogEntry;
import com.keuin.blame.data.enums.ActionType;
import com.keuin.blame.util.MinecraftUtil;
import com.keuin.blame.util.PrintUtil;
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

public class EventHandler implements AttackEntityHandler, BreakBlockHandler, UseBlockHandler, UseEntityHandler, UseItemHandler {

    public static final EventHandler INSTANCE = new EventHandler();

    private EventHandler() {
    }

    @Override
    public void onPlayerUseBlock(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
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
        SubmitWorker.INSTANCE.submit(entry);
        PrintUtil.broadcast("use_block; block_id=" + blockId + "; world=" + worldString);
        // TODO: 增加判断，事件触发的时候用户不一定真正使用了方块（也可能是无效的动作）。放置方块的时候也会触发这个事件
//        PrintUtil.broadcast(String.format("player %s use block %s", playerEntity.getName().getString(), world.getBlockState(blockHitResult.getBlockPos())));
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
        PrintUtil.broadcast("break_block; block_id=" + blockId + "; world=" + worldString);
//        PrintUtil.broadcast(String.format("player %s break block %s", playerEntity.getName().getString(), blockState));
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
        PrintUtil.broadcast("attack_entity; entity_id=" + entityId);
//        PrintUtil.broadcast(String.format("player %s attack entity %s", playerEntity.getName().getString(), entity));
    }

    @Override
    public void onPlayerUseEntity(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
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
        SubmitWorker.INSTANCE.submit(entry);
        PrintUtil.broadcast("use_entity; entity_id=" + entityId);
        // TODO: 增加判断，无效的时候也会触发这个事件
        // TODO: 增加cooldown，过滤掉两个相邻重复事件（时间间隔大概为20ms+）
        PrintUtil.broadcast(String.format("player %s use entity %s", playerEntity.getName().getString(), entity));
    }

    @Override
    public void onPlayerUseItem(PlayerEntity playerEntity, World world, Hand hand) {
        String itemId = Registry.ITEM.getId(playerEntity.getStackInHand(hand).getItem()).toString();
        LogEntry entry = LogEntryFactory.playerWithItem(
                playerEntity,
                MinecraftUtil.worldToString(world),
                itemId,
                ActionType.ITEM_USE
        );
        SubmitWorker.INSTANCE.submit(entry);
        PrintUtil.broadcast("use_item; item_id=" + itemId);
        // TODO: 增加cooldown，过滤掉两个相邻重复事件（时间间隔大概为20ms+）
//        PrintUtil.broadcast(String.format("player %s use item %s", playerEntity.getName().getString(), playerEntity.getStackInHand(hand)));
    }
}
