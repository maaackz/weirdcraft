package com.maaackz.weirdcraft.renderer;


import com.maaackz.weirdcraft.Weirdcraft;
import com.maaackz.weirdcraft.entity.HolyMackerelEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class HolyMackerelRenderer extends MobEntityRenderer<HolyMackerelEntity, SalmonEntityModel<HolyMackerelEntity>> {
    private static final Identifier TEXTURE = Identifier.of(Weirdcraft.MOD_ID, "textures/entity/fish/holy_mackerel.png");

    public HolyMackerelRenderer(EntityRendererFactory.Context context) {
        super(context, new SalmonEntityModel<>(context.getPart(EntityModelLayers.SALMON)), 0.4F);
    }

    @Override
    public Identifier getTexture(HolyMackerelEntity entity) {
        return TEXTURE;
    }

    protected void setupTransforms(HolyMackerelEntity holyMackerelEntity, MatrixStack matrixStack, float f, float g, float h, float i) {
        super.setupTransforms(holyMackerelEntity, matrixStack, f, g, h, i);
        float j = 1.0F;
        float k = 1.0F;
        if (!holyMackerelEntity.isTouchingWater()) {
            j = 1.3F;
            k = 1.7F;
        }

        float l = j * 4.3F * MathHelper.sin(k * 0.6F * f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(l));
        matrixStack.translate(0.0F, 0.0F, -0.4F);
        if (!holyMackerelEntity.isTouchingWater()) {
            matrixStack.translate(0.2F, 0.1F, 0.0F);
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
        }

    }
}
