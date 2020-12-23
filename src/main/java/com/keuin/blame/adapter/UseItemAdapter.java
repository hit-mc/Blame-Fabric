package com.keuin.blame.adapter;

import com.keuin.blame.adapter.handler.UseItemHandler;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class UseItemAdapter implements UseItemCallback {

    private final UseItemHandler handler;

    public UseItemAdapter(UseItemHandler handler) {
        this.handler = handler;
    }

    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity playerEntity, World world, Hand hand) {
        handler.onPlayerUseItem(playerEntity, world, hand);
        return TypedActionResult.pass(ItemStack.EMPTY);
    }
}
