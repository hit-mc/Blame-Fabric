package com.keuin.blame.adapter.handler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface PlaceBlockHandler {

    Event<PlaceBlockHandler> EVENT = EventFactory.createArrayBacked(PlaceBlockHandler.class,
            (listeners) -> (world, playerEntity, blockPos, blockState, blockEntity) -> {
                for (PlaceBlockHandler listener : listeners) {
                    listener.onPlayerPlaceBlock(world, playerEntity, blockPos, blockState, blockEntity);
                }
            });

    void onPlayerPlaceBlock(World world, @Nullable PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity);
}
