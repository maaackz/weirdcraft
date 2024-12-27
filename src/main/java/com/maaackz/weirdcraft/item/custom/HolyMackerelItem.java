package com.maaackz.weirdcraft.item.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class HolyMackerelItem extends EntityBucketItem {

    public HolyMackerelItem(EntityType<?> type, Fluid fluid, SoundEvent emptyingSound, Settings settings) {
        super(type, fluid, emptyingSound, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        // Use the bucket item normally
        TypedActionResult<ItemStack> result = super.use(world, player, hand);

//        if (result.getResult() == ActionResult.SUCCESS) {
            // Call the method to get the emptied stack (empty bucket)
//            ItemStack stackInHand = player.getStackInHand(hand);
//            ItemStack emptiedStack = getEmptiedStack(stackInHand, player);

            // Set the player's hand item to the empty bucket after use
//            if (!world.isClient) {
//                player.setStackInHand(hand, emptiedStack);
//            }
//        }

        return result;
    }
}
