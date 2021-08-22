package com.keuin.blame.adapter.handler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface PlaceBlockHandler {

    Event<PlaceBlockHandler> EVENT = EventFactory.createArrayBacked(PlaceBlockHandler.class,
            (listeners) -> (world, playerEntity, blockPos) -> {
                for (PlaceBlockHandler listener : listeners) {
                    listener.onEntityPlaceBlock(world, playerEntity, blockPos);
                }
            });

    void onEntityPlaceBlock(World world, @Nullable LivingEntity livingEntity, BlockPos blockPos);
}
