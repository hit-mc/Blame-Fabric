package com.keuin.blame.adapter;

import com.keuin.blame.adapter.handler.UseEntityHandler;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class UseEntityAdapter implements UseEntityCallback {

    private final UseEntityHandler handler;

    public UseEntityAdapter(UseEntityHandler handler) {
        this.handler = handler;
    }

    @Override
    public ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        handler.onPlayerUseEntity(playerEntity, world, hand, entity, entityHitResult);
        return ActionResult.PASS;
    }
}
