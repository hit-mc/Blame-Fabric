package com.keuin.blame.adapter;

import com.keuin.blame.adapter.handler.BreakBlockHandler;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BreakBlockAdapter implements PlayerBlockBreakEvents.After {

    private final BreakBlockHandler handler;

    public BreakBlockAdapter(BreakBlockHandler handler) {
        this.handler = handler;
    }

    @Override
    public void afterBlockBreak(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        handler.onPlayerBreakBlock(world, playerEntity, blockPos, blockState, blockEntity);
    }
}
