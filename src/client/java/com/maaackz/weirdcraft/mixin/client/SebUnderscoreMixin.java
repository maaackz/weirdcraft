package com.maaackz.weirdcraft.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepWoolFeatureRenderer.class)
public abstract class SebUnderscoreMixin extends FeatureRenderer<SheepEntity, SheepEntityModel<SheepEntity>> {

    public SebUnderscoreMixin(FeatureRendererContext<SheepEntity, SheepEntityModel<SheepEntity>> context) {
        super(context);
    }

    @Final
    @Shadow
    private SheepWoolEntityModel<SheepEntity> model;

    private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/sheep/sheep_fur.png");

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/SheepEntity;FFFFFF)V", at = @At("RETURN"))
    public void onRender(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SheepEntity sheepEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (!sheepEntity.isSheared()) {
            if (!sheepEntity.isInvisible()) {
                int u;
                if (sheepEntity.hasCustomName() && "seb_".equals(sheepEntity.getName().getString())) {
                    // Define colors for red and blue
                    int red = 0xFFAA0000;  // Red in ARGB
                    int blue = 0xFF0000AA; // Blue in ARGB

                    // Set as floats if finer control over timing is needed
                    float cycleDuration = 60.0f;
                    float solidColorDuration = 27.75f;
                    float transitionDuration = 2.25f;

// Calculate progress within the cycle
                    float cycleProgress = sheepEntity.age % cycleDuration;
                    float r;

                    if (cycleProgress < solidColorDuration) {
                        // Hold at Red
                        u = red;
                    } else if (cycleProgress < solidColorDuration + transitionDuration) {
                        // Transition from Red to Blue
                        r = (cycleProgress - solidColorDuration) / transitionDuration;
                        u = ColorHelper.Argb.lerp(r, red, blue);
                    } else if (cycleProgress < 2 * solidColorDuration + transitionDuration) {
                        // Hold at Blue
                        u = blue;
                    } else {
                        // Transition from Blue to Red
                        r = (cycleProgress - (2 * solidColorDuration + transitionDuration)) / transitionDuration;
                        u = ColorHelper.Argb.lerp(r, blue, red);
                    }


                    // Render with the updated color
                    render(this.getContextModel(), model, SKIN, matrixStack, vertexConsumerProvider, i, sheepEntity, f, g, j, k, l, h, u);
                } else {
                    if (!"jeb_".equals(sheepEntity.getName().getString())) {
                        // Default sheep color rendering
                        u = SheepEntity.getRgbColor(sheepEntity.getColor());
                    }
                }
            }
        }
    }
}
