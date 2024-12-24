package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.sound.CustomSounds;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class PapayaEatSoundMixin {

    @Shadow public abstract Text getName();

    @Shadow public abstract String getTranslationKey();

    @Inject(at = @At("HEAD"), method = "getEatSound", cancellable = true)
    private void onEat(CallbackInfoReturnable<SoundEvent> cir) {
        if (this.getTranslationKey().contains("melon"))
            cir.setReturnValue(CustomSounds.PAPAYA_EAT_SOUND);
    }


}
