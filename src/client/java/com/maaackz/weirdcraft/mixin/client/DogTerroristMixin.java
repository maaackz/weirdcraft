package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.Weirdcraft;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfCollarFeatureRenderer.class)
public abstract class DogTerroristMixin extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {

    private static final Identifier CUSTOM_SKIN = Identifier.of(Weirdcraft.MOD_ID, "textures/entity/wolf/wolf_ski_mask.png");

    public DogTerroristMixin(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context) {
        super(context);
    }

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/WolfEntity;FFFFFF)V", cancellable = true)
    private void onRender(
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int light,
            WolfEntity wolfEntity,
            float f,
            float g,
            float h,
            float j,
            float k,
            float l,
            CallbackInfo info) {

        if (wolfEntity.isTamed() && !wolfEntity.isInvisible()) {
            DyeColor collarColor = wolfEntity.getCollarColor();
            // Extract RGB components (0-15)

            // Check if the collar color is black (RGB: 0.11372549, 0.11372549, 0.12941177)
            if (!collarColor.toString().equals("red")) {
                // Change the SKIN to the CUSTOM_SKIN for black collar color
                renderModel(this.getContextModel(), CUSTOM_SKIN, matrixStack, vertexConsumerProvider, light, wolfEntity, 1); // Adjust the last parameter as needed
                info.cancel(); // Cancel the original render method
            } else {
                // Use the default skin for other colors
                // renderModel(this.getContextModel(), DEFAULT_SKIN, matrixStack, vertexConsumerProvider, light, wolfEntity, 1);
            }
        }
    }
}
