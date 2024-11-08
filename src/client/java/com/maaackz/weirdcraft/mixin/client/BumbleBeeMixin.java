package com.maaackz.weirdcraft.mixin.client;

import net.minecraft.client.render.entity.BeeEntityRenderer;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(BeeEntityRenderer.class)
public class BumbleBeeMixin {

    @Inject(at = @At("HEAD"), method = "getTexture(Lnet/minecraft/entity/passive/BeeEntity;)Lnet/minecraft/util/Identifier;", cancellable = true)
    private void init(BeeEntity beeEntity, CallbackInfoReturnable<Identifier> cir) throws IOException {
        if (beeEntity.hasCustomName() && "bumble".equals(beeEntity.getName().getString())) {
            if(beeEntity.hasAngerTime())
            {
                if(beeEntity.hasNectar())
                {
                    cir.setReturnValue(Identifier.of("weirdcraft","textures/entity/bee/bee_angry_nectar_alt.png"));
                }
                else
                {
                    cir.setReturnValue(Identifier.of("weirdcraft","textures/entity/bee/bee_angry_alt.png"));
                }
            }
            else
            {
                if(beeEntity.hasNectar()) {
                    cir.setReturnValue(Identifier.of("weirdcraft", "textures/entity/bee/bee_nectar_alt.png"));
                }
                else
                {
                    cir.setReturnValue(Identifier.of("weirdcraft","textures/entity/bee/bee_alt.png"));
                }
            }
        }
    }
}
