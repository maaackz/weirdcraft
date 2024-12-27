    package com.maaackz.weirdcraft.entity;

    import com.maaackz.weirdcraft.item.CustomItems;
    import net.minecraft.advancement.criterion.Criteria;
    import net.minecraft.block.BlockState;
    import net.minecraft.component.DataComponentTypes;
    import net.minecraft.component.type.NbtComponent;
    import net.minecraft.entity.Bucketable;
    import net.minecraft.entity.EntityType;
    import net.minecraft.entity.MovementType;
    import net.minecraft.entity.ai.control.MoveControl;
    import net.minecraft.entity.ai.pathing.EntityNavigation;
    import net.minecraft.entity.ai.pathing.SwimNavigation;
    import net.minecraft.entity.attribute.DefaultAttributeContainer;
    import net.minecraft.entity.attribute.EntityAttributes;
    import net.minecraft.entity.damage.DamageSource;
    import net.minecraft.entity.data.DataTracker;
    import net.minecraft.entity.data.TrackedData;
    import net.minecraft.entity.data.TrackedDataHandlerRegistry;
    import net.minecraft.entity.mob.WaterCreatureEntity;
    import net.minecraft.entity.passive.FishEntity;
    import net.minecraft.entity.player.PlayerEntity;
    import net.minecraft.item.ItemStack;
    import net.minecraft.nbt.NbtCompound;
    import net.minecraft.registry.tag.FluidTags;
    import net.minecraft.server.network.ServerPlayerEntity;
    import net.minecraft.sound.SoundEvent;
    import net.minecraft.sound.SoundEvents;
    import net.minecraft.util.ActionResult;
    import net.minecraft.util.Hand;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.util.math.MathHelper;
    import net.minecraft.util.math.Vec3d;
    import net.minecraft.world.World;

    public class HolyMackerelEntity extends FishEntity {

        private static final TrackedData<Boolean> FROM_BUCKET = DataTracker.registerData(HolyMackerelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

        public HolyMackerelEntity(EntityType<? extends FishEntity> entityType, World world) {
            super(entityType, world);
            this.moveControl = new HolyMackerelMoveControl(this);
        }

        public static DefaultAttributeContainer.Builder createAttributes() {
            return WaterCreatureEntity.createMobAttributes()
                    .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0);
        }

        public boolean cannotDespawn() {
            return super.cannotDespawn() || this.isFromBucket();
        }

        public boolean canImmediatelyDespawn(double distanceSquared) {
            return !this.isFromBucket() && !this.hasCustomName();
        }

        public int getLimitPerChunk() {
            return 8;
        }

        protected EntityNavigation createNavigation(World world) {
            return new SwimNavigation(this, world);
        }

        public void travel(Vec3d movementInput) {
            if (this.canMoveVoluntarily() && this.isTouchingWater()) {
                this.updateVelocity(0.01F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.9));
                if (this.getTarget() == null) {
                    this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
                }
            } else {
                super.travel(movementInput);
            }

        }

        public void tickMovement() {
            if (!this.isTouchingWater() && this.isOnGround() && this.verticalCollision) {
                this.setVelocity(this.getVelocity().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), 0.4000000059604645, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F)));
                this.setOnGround(false);
                this.velocityDirty = true;
                this.playSound(this.getFlopSound());
            }

            super.tickMovement();
        }

        @Override
        protected ActionResult interactMob(PlayerEntity player, Hand hand) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (this.isAlive()) {
                // Play the sound when the entity is "picked up"
                this.playSound(((Bucketable) this).getBucketFillSound(), 1.0F, 1.0F);

                // Create the custom item stack for the fish without involving a bucket
                ItemStack fishItemStack = ((Bucketable) this).getBucketItem();
                ((Bucketable) this).copyDataToStack(fishItemStack);

                // Add the item directly to the player's inventory
                if (!player.getInventory().insertStack(fishItemStack)) {
                    // If the inventory is full, drop the item into the world
                    this.dropStack(fishItemStack);
                }

                // Trigger the criteria if on the server
                World world = this.getWorld();
                if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.FILLED_BUCKET.trigger(serverPlayer, fishItemStack);
                }

                // Remove the entity from the world
                this.discard();
                return ActionResult.success(world.isClient);
            }

            return super.interactMob(player, hand);
        }

        @Override
        protected void tickWaterBreathingAir(int air) {
            if (this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
                this.setAir(300);
            } else {
                this.setAir(300);
            }

        }
        public void copyDataToStack(ItemStack stack) {
            Bucketable.copyDataToStack(this, stack);
        }

        public void copyDataFromNbt(NbtCompound nbt) {
            Bucketable.copyDataFromNbt(this, nbt);
        }

        public SoundEvent getBucketFillSound() {
            return SoundEvents.ITEM_BUCKET_FILL_FISH;
        }

        protected boolean hasSelfControl() {
            return true;
        }

        protected SoundEvent getSwimSound() {
            return SoundEvents.ENTITY_FISH_SWIM;
        }

        protected void playStepSound(BlockPos pos, BlockState state) {
        }

        static class HolyMackerelMoveControl extends MoveControl {
            private final HolyMackerelEntity fish;

            HolyMackerelMoveControl(HolyMackerelEntity owner) {
                super(owner);
                this.fish = owner;
            }

            public void tick() {
                if (this.fish.isSubmergedIn(FluidTags.WATER)) {
                    this.fish.setVelocity(this.fish.getVelocity().add(0.0, 0.005, 0.0));
                }

                if (this.state == State.MOVE_TO && !this.fish.getNavigation().isIdle()) {
                    float f = (float)(this.speed * this.fish.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                    this.fish.setMovementSpeed(MathHelper.lerp(0.125F, this.fish.getMovementSpeed(), f));
                    double d = this.targetX - this.fish.getX();
                    double e = this.targetY - this.fish.getY();
                    double g = this.targetZ - this.fish.getZ();
                    if (e != 0.0) {
                        double h = Math.sqrt(d * d + e * e + g * g);
                        this.fish.setVelocity(this.fish.getVelocity().add(0.0, (double)this.fish.getMovementSpeed() * (e / h) * 0.1, 0.0));
                    }

                    if (d != 0.0 || g != 0.0) {
                        float i = (float)(MathHelper.atan2(g, d) * 57.2957763671875) - 90.0F;
                        this.fish.setYaw(this.wrapDegrees(this.fish.getYaw(), i, 90.0F));
                        this.fish.bodyYaw = this.fish.getYaw();
                    }

                } else {
                    this.fish.setMovementSpeed(0.0F);
                }
            }
        }

        public ItemStack getBucketItem() {
            return new ItemStack(CustomItems.HOLY_MACKEREL);
        }

        protected SoundEvent getAmbientSound() {
            return SoundEvents.ENTITY_SALMON_AMBIENT;
        }

        protected SoundEvent getDeathSound() {
            return SoundEvents.ENTITY_SALMON_DEATH;
        }

        protected SoundEvent getHurtSound(DamageSource source) {
            return SoundEvents.ENTITY_SALMON_HURT;
        }

        protected SoundEvent getFlopSound() {
            return SoundEvents.ENTITY_SALMON_FLOP;
        }


        @Override
        protected void initDataTracker(DataTracker.Builder builder) {
            super.initDataTracker(builder);
            builder.add(FROM_BUCKET, false);
        }

        @Override
        public void writeCustomDataToNbt(NbtCompound nbt) {
            super.writeCustomDataToNbt(nbt);
            nbt.putBoolean("FromBucket", isFromBucket());
        }

        @Override
        public void readCustomDataFromNbt(NbtCompound nbt) {
            super.readCustomDataFromNbt(nbt);
            setFromBucket(nbt.getBoolean("FromBucket"));
        }

        @Override
        public void playAmbientSound() {
            this.playSound(SoundEvents.ENTITY_COD_AMBIENT, 0.5F, 1.0F);
        }

        @Override
        public boolean isFromBucket() {
            return dataTracker.get(FROM_BUCKET);
        }

        @Override
        public void setFromBucket(boolean fromBucket) {
            dataTracker.set(FROM_BUCKET, fromBucket);
        }

        @Override

        protected void dropLoot(DamageSource source, boolean causedByPlayer) {
            super.dropLoot(source, causedByPlayer);

            // Create a new bucket item with the same data
            ItemStack dropItem = this.getBucketItem(); // This creates the Holy Mackerel bucket

            dropItem.set(DataComponentTypes.CUSTOM_NAME, this.getCustomName());
            NbtComponent.set(DataComponentTypes.BUCKET_ENTITY_DATA, dropItem, (nbtCompound) -> {
                if (this.isAiDisabled()) {
                    nbtCompound.putBoolean("NoAI", this.isAiDisabled());
                }

                if (this.isSilent()) {
                    nbtCompound.putBoolean("Silent", this.isSilent());
                }

                if (this.hasNoGravity()) {
                    nbtCompound.putBoolean("NoGravity", this.hasNoGravity());
                }

                if (this.isGlowingLocal()) {
                    nbtCompound.putBoolean("Glowing", this.isGlowingLocal());
                }

                if (this.isInvulnerable()) {
                    nbtCompound.putBoolean("Invulnerable", this.isInvulnerable());
                }

                nbtCompound.putFloat("Health", this.getMaxHealth());
            });

            // Drop the item at the entity's location
            this.dropStack(dropItem);
        }



    }