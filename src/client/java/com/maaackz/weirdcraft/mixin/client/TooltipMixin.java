package com.maaackz.weirdcraft.mixin.client;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class TooltipMixin {

    @Shadow
    public abstract boolean isEmpty();

    @Shadow
    public abstract boolean isDamaged();

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract int getDamage();

    @Unique
    public Color color = new Color(255, 255, 255, 255);

    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void onGetTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {

        if (!this.isEmpty()) {
            ItemStack stack = (ItemStack) (Object) this; // Cast this to ItemStack
            Item item = stack.getItem();

            // Check if the item is an instance of ArmorItem and is boots
            if (item instanceof ArmorItem armorItem && armorItem.getSlotType().equals(ArmorItem.Type.BOOTS.getEquipmentSlot())) {

                if (this.isDamaged()) {
                    if (this.getDamage() == 1) {
                        Text toolTip = Text.of("baby shoes, worn once");
                        System.out.println(toolTip);
                        if (!cir.getReturnValue().contains(toolTip)) {
                            cir.getReturnValue().add(toolTip);
                        }
                    } else {
                        Text toolTip = Text.translatable("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()).setStyle(Style.EMPTY.withColor(color.getRGB()));
                        System.out.println(toolTip);
                        if (!cir.getReturnValue().contains(toolTip)) {
                            cir.getReturnValue().add(toolTip);
                        }
                    }

                } else {
                    Text toolTip = Text.of("baby shoes, never worn");
                    System.out.println(toolTip);
                    if (!cir.getReturnValue().contains(toolTip)) {
                        cir.getReturnValue().add(toolTip);
                    }
                }
            }
        }
    }
}
