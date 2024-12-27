package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.item.custom.HolyMackerelItem;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HolyMackerelItem.class)
public abstract class HolyMackerelNoBucketMixin extends BucketItem {

    private Fluid fluid;

    public HolyMackerelNoBucketMixin(Fluid fluid, Settings settings) {
        super(fluid, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        BlockState blockState;
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = raycast(world, user, this.fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
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

//    public static

}