package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.Weirdcraft;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatCollarFeatureRenderer.class)
public abstract class CatTerroristMixin extends FeatureRenderer<CatEntity, CatEntityModel<CatEntity>> {

    private static final Identifier CUSTOM_SKIN = Identifier.of(Weirdcraft.MOD_ID, "textures/entity/cat/cat_ski_mask.png");

    public CatTerroristMixin(FeatureRendererContext<CatEntity, CatEntityModel<CatEntity>> context) {
        super(context);
    }

//    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/CatEntity;FFFFFF)V", cancellable = true)
    private void onRender(
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int i,
            CatEntity catEntity,
            float f,
            float g,
            float h,
            float j,
            float k,
            float l,
            CallbackInfo info) {

        if (catEntity.isTamed() && !catEntity.isInvisible()) {
            DyeColor collarColor = catEntity.getCollarColor();
            // Extract RGB components (0-15)

            // Check if the collar color is black (RGB: 0.11372549, 0.11372549, 0.12941177)
            if (!collarColor.toString().equals("red")) {
                // Change the SKIN to the CUSTOM_SKIN for black collar color
                renderModel(this.getContextModel(), CUSTOM_SKIN, matrixStack, vertexConsumerProvider, 0, catEntity, 0); // Assuming '1' is the additional parameter needed
                info.cancel(); // Cancel the original render method
            } else {
                // Use the default skin for other colors
                // renderModel(this.getContextModel(), DEFAULT_SKIN, matrixStack, vertexConsumerProvider, i, catEntity, 1);
            }
        }
    }
}
