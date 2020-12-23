package com.keuin.blame.adapter.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public interface UseBlockHandler {
    void onPlayerUseBlock(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult);
}
