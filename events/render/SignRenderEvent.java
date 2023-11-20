package net.onyx.client.events.render;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.onyx.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SignRenderEvent extends Event {
    public SignBlockEntity signBlockEntity;
    public float f;
    public MatrixStack matrixStack;
    public VertexConsumerProvider vertexConsumerProvider;
    public int light;
    public int overlay;
    public CallbackInfo ci;

    public SignRenderEvent(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay, CallbackInfo ci) {
        this.signBlockEntity = signBlockEntity;
        this.f = f;
        this.matrixStack = matrixStack;
        this.vertexConsumerProvider = vertexConsumerProvider;
        this.light = light;
        this.overlay = overlay;
        this.ci = ci;
    }
}
