package com.keuin.blame.adapter;

import com.keuin.blame.adapter.handler.UseBlockHandler;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class UseBlockAdapter implements UseBlockCallback {

    private final UseBlockHandler handler;

    public UseBlockAdapter(UseBlockHandler handler) {
        this.handler = handler;
    }

    @Override
    public ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        handler.onPlayerUseBlock(playerEntity, world, hand, blockHitResult);
        return ActionResult.PASS;
    }
}
