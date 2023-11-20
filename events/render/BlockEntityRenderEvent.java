package net.onyx.client.events.render;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.onyx.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class BlockEntityRenderEvent extends Event {
    public BlockEntity blockEntity;
    public float tickDelta;
    public MatrixStack mStack;
    public VertexConsumerProvider arg3;
    public CallbackInfo ci;

    public BlockEntityRenderEvent(BlockEntity blockEntity, float tickDelta, MatrixStack mStack, VertexConsumerProvider arg3, CallbackInfo ci) {
        this.ci = ci;
        this.blockEntity = blockEntity;
        this.tickDelta = tickDelta;
        this.mStack = mStack;
        this.arg3 = arg3;
    }
}
