package com.maaackz.weirdcraft.item.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

public class HolyMackerelItem extends EntityBucketItem {

    public static UUID uuid;
    private final Fluid fluid;

    public HolyMackerelItem(EntityType<?> type, Fluid fluid, SoundEvent emptyingSound, Settings settings) {
        super(type, fluid, emptyingSound, settings);
        this.fluid = fluid;
    }

    @Override
    public SoundEvent getBreakSound() {
        return super.getBreakSound();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        BlockState blockState;
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = raycast(world, user, this.fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.ANY);
        BlockPos blockPos = blockHitResult.getBlockPos();
        Direction direction = blockHitResult.getSide();
        blockState = world.getBlockState(blockPos);
        BlockPos blockPos2 = blockPos.offset(direction);
        if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos2, direction, itemStack)) {
            BlockPos blockPos3 = blockState.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER ? blockPos : blockPos2;
            this.onEmptied(user, world, itemStack, blockPos3);
            if (user instanceof ServerPlayerEntity) {
                Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) user, blockPos3, itemStack);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(ItemStack.EMPTY,world.isClient());
        }
        return TypedActionResult.fail(ItemStack.EMPTY);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    public UUID getOwnerUuid() {
        // Get the bucket_entity_data component from the item
        NbtComponent component =
                Objects.requireNonNull(this.getComponents().get(DataComponentTypes.BUCKET_ENTITY_DATA));

        // Check if the "Owner" tag is present in the component's data
        if (component.contains("Owner")) {
            // Retrieve the Owner UUID from the component
            NbtCompound entityData = component.getNbt();
            return entityData.getUuid("Owner"); // Return the UUID
        } else {
            // Handle case where the "Owner" tag is not found
            return null; // Or throw an exception depending on your use case
        }
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        System.out.println("Holy Mackerel has been destroyed.");
        super.onItemEntityDestroyed(entity);
    }

}
