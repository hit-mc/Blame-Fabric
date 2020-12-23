package com.keuin.blame.adapter.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface UseItemHandler {
    void onPlayerUseItem(PlayerEntity playerEntity, World world, Hand hand);
}
