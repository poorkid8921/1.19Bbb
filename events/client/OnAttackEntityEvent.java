package net.onyx.client.events.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.onyx.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class OnAttackEntityEvent extends Event {
    public PlayerEntity player;
    public Entity target;
    public CallbackInfo ci;

    public OnAttackEntityEvent(PlayerEntity player, Entity target, CallbackInfo ci) {
        this.player = player;
        this.target = target;
        this.ci = ci;
    }
}
