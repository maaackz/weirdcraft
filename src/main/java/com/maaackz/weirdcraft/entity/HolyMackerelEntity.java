    package com.maaackz.weirdcraft.entity;

    import com.maaackz.weirdcraft.item.CustomItems;
    import com.maaackz.weirdcraft.util.DialogueHandler;
    import net.minecraft.block.BlockState;
    import net.minecraft.component.DataComponentTypes;
    import net.minecraft.component.type.NbtComponent;
    import net.minecraft.entity.*;
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
    import net.minecraft.server.ServerConfigHandler;
    import net.minecraft.server.network.ServerPlayerEntity;
    import net.minecraft.server.world.ServerWorld;
    import net.minecraft.sound.SoundEvent;
    import net.minecraft.sound.SoundEvents;
    import net.minecraft.util.ActionResult;
    import net.minecraft.util.Hand;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.util.math.MathHelper;
    import net.minecraft.util.math.Vec3d;
    import net.minecraft.world.World;
    import org.jetbrains.annotations.Nullable;

    import java.util.Optional;
    import java.util.UUID;

    public class HolyMackerelEntity extends FishEntity implements Tameable {

        private static final TrackedData<Boolean> FROM_BUCKET = DataTracker.registerData(HolyMackerelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        private static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(HolyMackerelEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        private static UUID tempUUID;
        public HolyMackerelEntity(EntityType<? extends FishEntity> entityType, World world) {
            super(entityType, world);
            this.moveControl = new HolyMackerelMoveControl(this);
//            this.initDataTracker(new DataTracker.Builder(this));
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

        @Override

        public void tickMovement() {
            if (!this.isTouchingWater() && this.isOnGround() && this.verticalCollision) {
                this.setVelocity(this.getVelocity().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), 0.4000000059604645, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F)));
                this.setOnGround(false);
                this.velocityDirty = true;
                this.playSound(this.getFlopSound());
                System.out.println(getOwner());
            }

            super.tickMovement();
        }

        @Override
        public ActionResult interactMob(PlayerEntity player, Hand hand) {
            if (this.getWorld() instanceof ServerWorld sw) {
//                setOwner(player);
//                Scoreboard scoreboard = sw.getScoreboard();

//                String objectiveName = "MACKEREL_OWNER";
//                var objective = scoreboard.getNullableObjective(objectiveName);
//                if (objective != null) {
//                    if (objective.getDisplayName().equals(Text.literal("OWNERLESS"))) {
//                        objective.setDisplayName(player.getName());
//                    }
//                } else {
//                    scoreboard.addObjective(
//                            objectiveName,
//                            ScoreboardCriterion.DUMMY,
//                            Text.literal("OWNERLESS"),
//                            ScoreboardCriterion.RenderType.INTEGER,
//                            false,
//                            null
//                    );
//                    if (objective.getDisplayName().equals(Text.literal("OWNERLESS"))) {
//                        objective.setDisplayName(player.getName());
//                    }
//                }


                    dataTracker.set(OWNER,Optional.of(player.getUuid()), true);
                System.out.println("Player UUID: " + player.getUuidAsString());
                tempUUID = player.getUuid();
                System.out.println("Owner UUID: " + tempUUID.toString());
                tempUUID = player.getUuid();
                // Create the custom item stack for the fish without involving a bucket
                ItemStack fishItemStack = ((Bucketable) this).getBucketItem();
                ((Bucketable) this).copyDataToStack(fishItemStack);

                // Try to insert the item into the offhand first
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    if (serverPlayer.getStackInHand(Hand.OFF_HAND) == ItemStack.EMPTY) {
                        serverPlayer.setStackInHand(Hand.OFF_HAND, fishItemStack);

                    }
                }


//                if (OWNER.toString().equals("Optional.empty")) {;
//                }


                if (!(player.getStackInHand(Hand.OFF_HAND).getItem().getTranslationKey().contains("holy_mackerel"))) {
                    // If the offhand is full, try to insert it into the main inventory
                    boolean addedToInventory = player.getInventory().insertStack(fishItemStack);
                    if (!addedToInventory) {
                        // If the inventory is full, drop the item into the world
                        this.dropStack(fishItemStack);
                    }
                }

                // Remove the entity from the world
                this.discard();
                return ActionResult.SUCCESS;
            }

            return ActionResult.FAIL;
        }

//
//        public void setMackerelOwner(PlayerEntity player) {
//            // Ensure the world has a scoreboard
//            if (this.getWorld() instanceof ServerWorld serverWorld) {
//                Scoreboard scoreboard = serverWorld.getScoreboard();
//
//                // Create the "mackerelOwner" objective if it doesn't exist
//                String objectiveName = "MACKEREL_OWNER";
//                var objective = scoreboard.getNullableObjective(objectiveName);
//
//                if (objective == null) {
//                    scoreboard.addObjective(
//                            objectiveName,
//                            ScoreboardCriterion.DUMMY,
//                            Text.literal("OWNERLESS"),
//                            ScoreboardCriterion.RenderType.INTEGER,
//                            false,
//                            null
//                    );
//                    if (objective.getDisplayName().equals(Text.literal("OWNERLESS"))) {
//                        objective.setDisplayName(player.getName());
//                    }
//                }
//
//                // Set the player's UUID as the value for this entity in the scoreboard
//                if (objective != null) {
//                    System.out.println("Setting score display text: " + player.getName());
//                    var score = scoreboard.getOrCreateScore(ScoreHolder.fromName("MACKEREL_OWNER"), objective);
////                    int encodedUuid = Integer.parseInt(player.getUuidAsString()); // Encode the player's UUID as an integer
//                    score.setDisplayText(player.getName()); // Store the encoded UUID
//                    System.out.println(score.getDisplayText());
//                }
//            }
//        }

        @Override
        protected void tickWaterBreathingAir(int air) {
            if (this.isAlive()) {
                this.setAir(300);
            } else {
                this.setAir(300);
            }
        }
        @Override
        public void copyDataToStack(ItemStack stack) {
            stack.set(DataComponentTypes.CUSTOM_NAME, this.getCustomName());
            NbtComponent.set(DataComponentTypes.BUCKET_ENTITY_DATA, stack, (nbtCompound) -> {
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
                    nbtCompound.putBoolean("Glowing", true);
                }

                if (this.isInvulnerable()) {
                    nbtCompound.putBoolean("Invulnerable", this.isInvulnerable());
                }

                nbtCompound.putFloat("Health", this.getHealth());
                nbtCompound.putUuid("Owner", tempUUID); // Save the UUID only if present
            });



        }
        @Override
        public void copyDataFromNbt(NbtCompound nbt) {
            Bucketable.copyDataFromNbt(this, nbt);
//            if (nbt.contains("Owner")) {
//                System.out.println("OWNER DATA FOUND 2" + nbt.getUuid(String.valueOf(nbt.getUuid("Owner"))));
//                dataTracker.set(OWNER,Optional.of(nbt.getUuid("Owner")));
//            }
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
//                System.out.println("Player UUID: " + player.getUuidAsString());
//                if (entity.getDataTracker().get(OWNER).isPresent() ) {
//                    System.out.println("Owner UUID: " + entity.getDataTracker().get(OWNER).get());
//                }

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

        @Override
        public void onDeath(DamageSource damageSource) {
            if (!this.getWorld().isClient) { // Ensure this runs only on the server side
                ServerPlayerEntity nearestPlayer = (ServerPlayerEntity) this.getWorld().getClosestPlayer(this.getX(), this.getY(), this.getZ(), 25.0, false);
                if (nearestPlayer != null) {
                    DialogueHandler.sendDialogue(nearestPlayer, "mackerel", "death");
                }
            }
            super.onDeath(damageSource);
        }


        @Override
        public boolean damage(DamageSource source, float amount) {
            // Find the nearest player
            if (!this.getWorld().isClient) { // Ensure this runs only on the server side
                ServerPlayerEntity nearestPlayer = (ServerPlayerEntity) this.getWorld().getClosestPlayer(this.getX(), this.getY(), this.getZ(), 25.0, false);
                if (nearestPlayer != null) {
                    DialogueHandler.sendDialogue(nearestPlayer, "mackerel", "take_damage");
                }
            }
            return super.damage(source, amount);
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
            System.out.println("Holy Mackerel data tracker initiated.");
            super.initDataTracker(builder);
            builder.add(FROM_BUCKET, false);
            builder.add(OWNER, Optional.empty());
            builder.build();

        }
        @Override
        public void writeCustomDataToNbt(NbtCompound tag) {
            super.writeCustomDataToNbt(tag);
            tag.putBoolean("FromBucket", isFromBucket());
            if (this.getOwnerUuid() != null) {
                tag.putUuid("Owner", this.getOwnerUuid());
            }
        }
//
//        @Override
//        public void writeCustomDataToNbt(NbtCompound nbt) {
//
//            nbt.putBoolean("FromBucket", isFromBucket());
//            System.out.println("OWNER DATA FOUND 3" + nbt.getUuid(String.valueOf(nbt.getUuid("Owner"))));
//            getOwner().ifPresent(owner -> nbt.putUuid("Owner", owner)); // Save the UUID only if present
//            super.writeCustomDataToNbt(nbt);
//        }

        @Override
        public void readCustomDataFromNbt(NbtCompound tag) {
            super.readCustomDataFromNbt(tag);
            UUID uUID2;
            if (tag.containsUuid("Owner")) {
                uUID2 = tag.getUuid("Owner");
            } else {
                String string = tag.getString("Owner");
                uUID2 = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
            }
            if (uUID2 != null) {
                try {
                    this.setOwnerUuid(uUID2);
                    this.setTamed(true);
                } catch (Throwable var4) {
                    this.setTamed(false);
                }
            }
            setFromBucket(tag.getBoolean("FromBucket"));
        }

        private void setTamed(boolean b) {
        }


        @Override
        public void playAmbientSound() {
            this.playSound(SoundEvents.ENTITY_COD_AMBIENT, 0.5F, 1.0F);
        }

        @Override
        public boolean isFromBucket() {
            return dataTracker.get(FROM_BUCKET);
        }

        @Nullable
        @Override
        public UUID getOwnerUuid() {
            return (UUID) ((Optional<UUID>) this.dataTracker.get(OWNER)).orElse((UUID) (Object) null);
        }

        public void setOwnerUuid(@Nullable UUID uuid) {

//            this.dataTracker.set(OWNER, Optional.ofNullable(uuid));
        }
//
//        @Override
//        public void setOwner(PlayerEntity player) {
//            // Ensure the world has a scoreboard
//
//        }


        @Nullable
        @Override
        public LivingEntity getOwner() {
            try {
                UUID uUID = this.getOwnerUuid();
                return uUID == null ? null : this.getWorld().getPlayerByUuid(uUID);
            } catch (IllegalArgumentException var2) {
                return null;
            }
        }

        public boolean isOwner(LivingEntity entity) {
            return entity == this.getOwner();
        }

        @Override
        public void setFromBucket(boolean fromBucket) {
            dataTracker.set(FROM_BUCKET, fromBucket);
        }

//        public void setOwner(PlayerEntity player) {
//            this.setTamed(true);
//            this.setOwnerUuid(player.getUuid());
////            dataTracker.set(OWNER, ()), true);
//        }

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
                    nbtCompound.putBoolean("Glowing", true);
                }

                if (this.isInvulnerable()) {
                    nbtCompound.putBoolean("Invulnerable", this.isInvulnerable());
                }
//                nbtCompound.putUuid("Owner", getOwnerUuid());
                nbtCompound.putFloat("Health", this.getMaxHealth());
            });

            // Drop the item at the entity's location
            this.dropStack(dropItem);
        }



    }