package com.maaackz.weirdcraft.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.UUID;

@Mixin(SheepEntity.class)
public abstract class SebHostileMixin extends AnimalEntity {

    private boolean hasBeenNamedSeb = false;

    protected SebHostileMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "createSheepAttributes", at = @At("RETURN"), cancellable = true)
    private static void modifyAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(cir.getReturnValue()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0)
        );
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"))
    private void onHurt(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        SheepEntity sheepEntity = (SheepEntity) (Object) this;

        if (!hasBeenNamedSeb && sheepEntity.hasCustomName() && "seb_".equals(sheepEntity.getName().getString())) {
            hasBeenNamedSeb = true;

            if (source.getAttacker() instanceof LivingEntity attacker) {
                UUID attackerUUID = attacker.getUuid();
                UUID excludedUUID = UUID.fromString("4a531d63-700a-4407-92d1-3b519a09ab6c");

                if (!attackerUUID.equals(excludedUUID)) {
                    // Send "makeangry" signal to nearby sheep
                    this.getWorld().getEntitiesByClass(SebHostileMixin.class, this.getBoundingBox().expand(10), entity -> entity != this)
                            .forEach(nearbySheep -> {
                                if (nearbySheep.getBrain() != null) {
                                    // Send signal to nearby sheep to become angry and target the attacker
                                    nearbySheep.getBrain().remember(MemoryModuleType.HURT_BY_ENTITY, Optional.of(attacker));
                                    System.out.println("Signal sent to nearby sheep: " + nearbySheep.getName().getString() + " to attack: " + attackerUUID);

                                    // Call makeAngry on the nearby sheep
                                    nearbySheep.makeAngry(attackerUUID);

                                    // Force the nearby sheep to reevaluate its goals and target the attacker
                                    nearbySheep.goalSelector.add(1, new ActiveTargetGoal<>(nearbySheep, LivingEntity.class, true));
                                    nearbySheep.goalSelector.tickGoals(true);
                                }
                            });

                    // Set memory for the main sheep to remember the attacker
                    this.getBrain().remember(MemoryModuleType.HURT_BY_ENTITY, Optional.of(attacker));
                    System.out.println("Memory set for sheep: " + sheepEntity.getName().getString() + " to attack: " + attackerUUID);

                    // Call makeAngry on the main sheep
                    this.makeAngry(attackerUUID);
                }
            }
        }
    }

    @Unique
    public void makeAngry(UUID attackerUUID) {
        SheepEntity sheepEntity = (SheepEntity) (Object) this;
        System.out.println("received " + this + " is angry");

        // Add ActiveTargetGoal if not already present
        ActiveTargetGoal<LivingEntity> activeTargetGoal = new ActiveTargetGoal<>(sheepEntity, LivingEntity.class, true) {
            @Override
            public boolean canStart() {
                // Check if the sheep has memory of being hurt by the attacker
                boolean shouldTarget = sheepEntity.getBrain() != null && sheepEntity.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY_ENTITY) &&
                        sheepEntity.getBrain().getOptionalMemory(MemoryModuleType.HURT_BY_ENTITY)
                                .map(target -> target.getUuid().equals(attackerUUID))
                                .orElse(false);
                if (!shouldTarget) {
                    System.out.println(sheepEntity.getName().getString() + " is not targeting: " + attackerUUID);
                }
                return shouldTarget; // Ensure targeting happens when conditions are met
            }
        };
        this.goalSelector.add(1, activeTargetGoal);

        // Add MeleeAttackGoal if not already present
        MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 2, true);
        this.goalSelector.add(2, meleeAttackGoal);

        // Add RevengeGoal if not already present
//        RevengeGoal revengeGoal = new RevengeGoal(this);
//        revengeGoal.setGroupRevenge(SheepEntity.class);
//        this.goalSelector.add(3, revengeGoal);
    }
}
