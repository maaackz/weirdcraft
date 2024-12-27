package com.maaackz.weirdcraft.mixin.client;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class HideCustomNameMixin {

    @Shadow public abstract Item getItem();

    // Inject into the method where the sleep state is changed
    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void onGetName(CallbackInfoReturnable<Text> cir) {
        if (this.getItem().getTranslationKey().contains("holy_mackerel")) {
            cir.cancel();
            cir.setReturnValue(Text.of("Holy Mackerel"));
        }
    }

}
