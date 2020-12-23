package com.keuin.blame.adapter;

import com.keuin.blame.adapter.handler.AttackEntityHandler;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AttackEntityAdapter implements AttackEntityCallback {

    private final AttackEntityHandler handler;

    public AttackEntityAdapter(AttackEntityHandler handler) {
        this.handler = handler;
    }

    @Override
    public ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        handler.onPlayerAttackEntity(playerEntity, world, hand, entity, entityHitResult);
        return ActionResult.PASS;
    }
}
