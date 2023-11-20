package net.onyx.client.events.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.onyx.client.events.Event;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class RenderItemEvent extends Event {
    public LivingEntity entity;
    public ItemStack stack;
    public ModelTransformation.Mode renderMode;
    public boolean leftHanded;
    public MatrixStack mStack;
    public VertexConsumerProvider vertexConsumers;
    public int light;
    public CallbackInfo ci;

    public RenderItemEvent(
        LivingEntity entity,
        ItemStack stack,
        ModelTransformation.Mode renderMode,
        boolean leftHanded,
        MatrixStack mStack,
        VertexConsumerProvider vertexConsumers,
        int light,
        CallbackInfo ci
    ) {
        this.entity = entity;
        this.stack = stack;
        this.renderMode = renderMode;
        this.leftHanded = leftHanded;
        this.mStack = mStack;
        this.vertexConsumers = vertexConsumers;
        this.light = light;
        this.ci = ci;
    }
}
