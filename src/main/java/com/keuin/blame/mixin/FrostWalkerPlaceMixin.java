package com.keuin.blame.mixin;

import com.keuin.blame.adapter.handler.PlaceBlockHandler;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FrostWalkerEnchantment.class)
public abstract class FrostWalkerPlaceMixin {

    @Inject(locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"),
            method = "freezeWater")
    private static void applyMovementEffectsMixin(LivingEntity entity, World world, BlockPos blockPos, int level,
                                                  CallbackInfo ci, BlockState blockState3) {
        PlaceBlockHandler.EVENT.invoker().onEntityPlaceBlock(
                world,
                entity,
                blockPos
        );
    }
}
