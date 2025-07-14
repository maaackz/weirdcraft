package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.item.CustomItems;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(HeldItemRenderer.class)
public abstract class ViewModelMixin {

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"), cancellable = true)
    private void onRenderItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (item.getItem() == CustomItems.HOLY_MACKEREL) {
            if (hand == Hand.MAIN_HAND) {
//                matrices.translate(VMScreen.CONFIG.instance().posX, VMScreen.CONFIG.instance().posY, VMScreen.CONFIG.instance().posZ);
//                matrices.scale(VMScreen.CONFIG.instance().scale, VMScreen.CONFIG.instance().scale, VMScreen.CONFIG.instance().scale);
//                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(VMScreen.CONFIG.instance().rotMainX));
//                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(VMScreen.CONFIG.instance().rotMainY));
//                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(VMScreen.CONFIG.instance().rotMainZ));
                matrices.translate(0.23F, 0.0F, 0.0F); // y-0.07
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(15.0F)); // + 5.2
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-140.0F));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0.0F)); // +9.92
            } else {
                matrices.translate(0.15F, -0.07F, 0.0F); // y-0.07
//                matrices.scale(VMScreen.CONFIG.instance().scale, VMScreen.CONFIG.instance().scale, VMScreen.CONFIG.instance().scale);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(58.59F)); // + 5.2
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-16.54F));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(35.9F)); // +9.92
            }
        } else if (item.getItem() == CustomItems.NOCTURNES_KISS) {
            if (hand == Hand.MAIN_HAND) {
//                matrices.translate(VMScreen.CONFIG.instance().posX, VMScreen.CONFIG.instance().posY, VMScreen.CONFIG.instance().posZ);
//                matrices.scale(VMScreen.CONFIG.instance().scale, VMScreen.CONFIG.instance().scale, VMScreen.CONFIG.instance().scale);
//                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(VMScreen.CONFIG.instance().rotMainX));
//                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(VMScreen.CONFIG.instance().rotMainY));
//                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(VMScreen.CONFIG.instance().rotMainZ));
            } else {
                matrices.translate(-0.04f, -0.04f, 0.0F); // y-0.07
//                matrices.scale(VMScreen.CONFIG.instance().scale, VMScreen.CONFIG.instance().scale, VMScreen.CONFIG.instance().scale);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(10.87F)); // + 5.2
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-41.1F));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(30.71F)); // +9.92
            }
        }
    }

//    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderArmHoldingItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IFFLnet/minecraft/util/Arm;)V"), cancellable = true)
//    private void onRenderArm(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
//        matrices.translate(VMScreen.CONFIG.instance().posArmX, VMScreen.CONFIG.instance().posArmY, VMScreen.CONFIG.instance().posArmZ);
//        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(VMScreen.CONFIG.instance().rotArmX));
//        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(VMScreen.CONFIG.instance().rotArmY));
//        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(VMScreen.CONFIG.instance().rotArmZ));
//    }
}