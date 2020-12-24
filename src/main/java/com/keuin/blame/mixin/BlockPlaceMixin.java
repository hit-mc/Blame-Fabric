package com.keuin.blame.mixin;

import com.keuin.blame.adapter.handler.PlaceBlockHandler;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.PlacedBlockCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Custom event driver for player placing blocks
 */
@Mixin(PlacedBlockCriterion.class)
public abstract class BlockPlaceMixin extends AbstractCriterion<PlacedBlockCriterion.Conditions> {

	@Inject(at = @At(value = "HEAD"), method = "trigger")
	public void trigger(ServerPlayerEntity player, BlockPos blockPos, ItemStack stack, CallbackInfo ci) {
		PlaceBlockHandler.EVENT.invoker().onPlayerPlaceBlock(
				player.world,
				player,
				blockPos,
				player.world.getBlockState(blockPos),
				player.world.getBlockEntity(blockPos)
		);
	}
}