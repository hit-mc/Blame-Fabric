package com.keuin.blame.adapter.handler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;

public interface EndGatewayTeleportHandler {
    Event<EndGatewayTeleportHandler> EVENT = EventFactory.createArrayBacked(EndGatewayTeleportHandler.class,
            (listeners) -> (gatewayBlockEntity, teleportedEntity) -> {
                for (var listener : listeners) {
                    listener.onEndGatewayTeleport(gatewayBlockEntity, teleportedEntity);
                }
            });

    void onEndGatewayTeleport(EndGatewayBlockEntity gatewayBlockEntity, Entity teleportedEntity);
}
