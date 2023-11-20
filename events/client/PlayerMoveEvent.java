package net.onyx.client.events.client;

import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.onyx.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PlayerMoveEvent extends Event {
    public MovementType type;
    public Vec3d offset;
    public CallbackInfo ci;
    
    public PlayerMoveEvent(MovementType type, Vec3d offset, CallbackInfo ci) {
        this.type = type;
        this.offset = offset;
        this.ci = ci;
    }
}
