package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.item.CustomItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public abstract class ComposterPoopMixin extends Block {

    public ComposterPoopMixin(Settings settings) {
        super(settings);
    }

    @Shadow public abstract SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos);

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setToDefaultPickupDelay()V"), method = "emptyFullComposter")
    private static void onEmptyFullComposter(Entity user, BlockState state, World world, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {

        if (!world.isClient) {
            // Get the inventory of the composter block
            ComposterBlock composterBlock = (ComposterBlock) state.getBlock();
            SidedInventory inventory = composterBlock.getInventory(state, world, pos);

            // Set your item of interest and chance
            ItemStack targetItem = new ItemStack(Items.SUNFLOWER);  // Change to desired item
            float chanceToSpawn = 1f;  // 10% chance to spawn the item per each instance

            // Loop through the inventory and apply the chance per item stack
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.isEmpty() && stack.getItem() == targetItem.getItem()) {
                    // Roll the dice to determine if we spawn the item
                    if (world.random.nextFloat() < chanceToSpawn) {
                        Vec3d vec3d = Vec3d.add(pos, 0.5, 1.01, 0.5).addRandom(world.random, 0.7F);
                        ItemEntity itemEntity = new ItemEntity(world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), new ItemStack(CustomItems.POOP));
                        itemEntity.setToDefaultPickupDelay();
                        world.spawnEntity(itemEntity);
                    }
                }
            }
        }
    }


}
