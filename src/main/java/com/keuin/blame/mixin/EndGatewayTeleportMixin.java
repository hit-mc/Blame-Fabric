package com.keuin.blame.mixin;

import com.keuin.blame.adapter.handler.EndGatewayTeleportHandler;
import com.keuin.blame.util.EntityUtil;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndGatewayBlockEntity.class)
public class EndGatewayTeleportMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;teleport(DDD)V"),
            method = "tryTeleportingEntity")
    public void teleportEntity(Entity entity, CallbackInfo ci) {
        // all teleported entities, including all passengers
        var entitySet = EntityUtil.getEntityRidingRelationClosure(entity.getRootVehicle());
        var gatewayBlockEntity = (EndGatewayBlockEntity) (Object) this;
        for (Entity e : entitySet) {
            EndGatewayTeleportHandler.EVENT
                    .invoker()
                    .onEndGatewayTeleport(gatewayBlockEntity, e);
        }
    }
}
