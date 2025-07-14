package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.Weirdcraft;
import com.maaackz.weirdcraft.item.CustomItems;
import com.maaackz.weirdcraft.item.custom.HolyMackerelItem;
import com.maaackz.weirdcraft.util.DialogueHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class HolyMackerelMixin {

    @Shadow public abstract void readCustomDataFromNbt(NbtCompound nbt);

    @Shadow public abstract ServerWorld getServerWorld();

    @Unique
    private boolean isRainBuffActive = false;
    @Unique
    private boolean isWaterBuffActive = false;
    @Unique
    private static Method isBeingRainedOnMethod;

    @Unique
    private static final long MESSAGE_COOLDOWN = 60000; // 5 seconds cooldown
    @Unique
    private long lastMessageTime = 0; // Timestamp of the last message sent

    // Map to track which players have received the "meet" dialogue (by UUID)
    @Unique
    private static final Map<UUID, Boolean> playerDialogueStatus = new HashMap<>();

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        boolean isRainingOnPlayer = isBeingRainedOn(player);
        boolean isTouchingWater = player.isTouchingWaterOrRain() || player.isWet() || player.isSubmergedInWater() || player.isInsideWaterOrBubbleColumn() || player.isSwimming();

        // Manage buffs based on water and rain states
        handleRainBuff(player, isRainingOnPlayer);
        handleWaterBuff(player, isTouchingWater);

        // Custom logic for Holy Mackerel item
        Scoreboard scoreboard = getServerWorld().getScoreboard();

        String objectiveName = "MACKEREL_OWNER";
        ScoreboardObjective objective = scoreboard.getNullableObjective(objectiveName);

        if (isWieldingHolyMackerel(player)) {
            // Check if the player has already received the "meet" dialogue
            if (!hasReceivedMeetDialogue(player)) {
                System.out.println("Met Rei.");

                // Ensure the objective exists
                if (objective == null) {
                    // Create the objective if it doesn't exist
                    objective = scoreboard.addObjective(
                            objectiveName,
                            ScoreboardCriterion.DUMMY,
                            Text.literal("OWNERLESS"),
                            ScoreboardCriterion.RenderType.INTEGER,
                            false,
                            null
                    );
                }

                // Check if the objective's display name is "OWNERLESS"
                if (objective != null ) {
                    if (objective.getDisplayName().equals(Text.literal("OWNERLESS"))) {
                        objective.setDisplayName(player.getName());
                        // Send the meet dialogue after delay
                        DialogueHandler.sendDialogueWithDelays(player, "mackerel", "meet");
                    } else {
                        DialogueHandler.sendDialogueWithDelays(player, "mackerel", "meet_non_owner");
                    }

                }

                // Ensure the player is marked as having received the dialogue
                lastMessageTime = System.currentTimeMillis(); // Update the last message time
                setHasReceivedMeetDialogue(player); // Mark the player as having received the dialogue
            } else {
                if (canSendDialogue()) {
                    if (objective != null && Objects.equals(player.getName().toString(), objective.getDisplayName().toString())) {
                        System.out.println("OWNER MATCHED TO HOLY MACKEREL");
                        DialogueHandler.sendDialogue(player, "mackerel", "random");
                        lastMessageTime = System.currentTimeMillis(); // Update the last message time
                    }
                }
            }
        } else {
            removeRainBuff(player);
            removeWaterBuff(player);
        }
    }



    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            if (source.getAttacker() != null) {
                DialogueHandler.sendDialogue(player, "mackerel", "player_take_damage");
            }

        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void uponDying(CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            DialogueHandler.sendDialogue(player, "mackerel", "owner_death");
        }
    }

    @Unique
    private boolean canSendDialogue() {
        return System.currentTimeMillis() - lastMessageTime > MESSAGE_COOLDOWN;
    }

    // Check if the player has already received the "meet" dialogue
    @Unique
    private boolean hasReceivedMeetDialogue(ServerPlayerEntity player) {
        return playerDialogueStatus.getOrDefault(player.getUuid(), false);
    }

    // Mark that the player has received the "meet" dialogue
    @Unique
    private void setHasReceivedMeetDialogue(ServerPlayerEntity player) {
        playerDialogueStatus.put(player.getUuid(), true);
    }

    @Unique
    private void handleRainBuff(ServerPlayerEntity player, boolean isRaining) {
        if (isRaining && !isRainBuffActive && isWieldingHolyMackerel(player)) {

            applyRainBuff(player); // 20% damage boost in rain

            DialogueHandler.sendDialogue(player, "mackerel", "rain_buff");
            isRainBuffActive = true;
        } else if (!isRaining && isRainBuffActive ) {
            removeRainBuff(player);
            isRainBuffActive = false;
        }
    }

    @Unique
    private void handleWaterBuff(ServerPlayerEntity player, boolean isTouchingWater) {
        if (isTouchingWater && !isWaterBuffActive && isWieldingHolyMackerel(player)) {
            isWaterBuffActive = true;
            applyWaterBuff(player); // 10% speed boost in water
            DialogueHandler.sendDialogue(player, "mackerel", "water_buff");
        } else if (!isTouchingWater && isWaterBuffActive ) {
            removeWaterBuff(player);
            isWaterBuffActive = false;
        }
    }

    @Unique
    private void applyRainBuff(ServerPlayerEntity player) {
//
//        EntityAttributeModifier water_atk_buff = new EntityAttributeModifier(
//                Identifier.of(Weirdcraft.MOD_ID, "water_atk_buff"),
//                1.25,
//                EntityAttributeModifier.Operation.ADD_VALUE
//        );
//
//        EntityAttributeModifier water_atk_speed_buff = new EntityAttributeModifier(
//                Identifier.of(Weirdcraft.MOD_ID, "water_atk_speed_buff"),
//                1.25,
//                EntityAttributeModifier.Operation.ADD_VALUE
//        );
//
//        EntityAttributeModifier water_kb_buff = new EntityAttributeModifier(
//                Identifier.of(Weirdcraft.MOD_ID, "water_kb_buff"),
//                1.25,
//                EntityAttributeModifier.Operation.ADD_VALUE
//        );
//
//        EntityAttributeModifier water_luck_buff = new EntityAttributeModifier(
//                Identifier.of(Weirdcraft.MOD_ID, "water_luck_buff"),
//                2,
//                EntityAttributeModifier.Operation.ADD_VALUE
//        );
//
//        EntityAttributeModifier water_movement_buff = new EntityAttributeModifier(
//                Identifier.of(Weirdcraft.MOD_ID, "water_movement_buff"),
//                2,
//                EntityAttributeModifier.Operation.ADD_VALUE
//        );
//
//        EntityAttributeModifier water_mining_buff = new EntityAttributeModifier(
//                Identifier.of(Weirdcraft.MOD_ID, "water_mining_buff"),
//                2,
//                EntityAttributeModifier.Operation.ADD_VALUE
//        );
//
//        EntityAttributeModifier water_armor_buff = new EntityAttributeModifier(
//                Identifier.of(Weirdcraft.MOD_ID, "water_armor_buff"),
//                1.25,
//                EntityAttributeModifier.Operation.ADD_VALUE
//        );
//
//        EntityAttributeModifier water_armor_toughness_buff = new EntityAttributeModifier(
//                Identifier.of(Weirdcraft.MOD_ID, "water_armor_toughness_buff"),
//                1.25,
//                EntityAttributeModifier.Operation.ADD_VALUE
//        );
//
//        EntityAttributeModifier water_speed_buff = new EntityAttributeModifier(
//                Identifier.of(Weirdcraft.MOD_ID, "water_speed_buff"),
//                1.25,
//                EntityAttributeModifier.Operation.ADD_VALUE
//        );
//
//        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_atk_buff"))) {
//            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
//                    .addPersistentModifier(water_atk_buff);
//        }
//        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_atk_speed_buff"))) {
//            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
//                    .addPersistentModifier(water_atk_speed_buff);
//        }
//        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_kb_buff"))) {
//            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK))
//                    .addPersistentModifier(water_kb_buff);
//        }
//        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_LUCK).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_luck_buff"))) {
//            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_LUCK))
//                    .addPersistentModifier(water_luck_buff);
//        }
//        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_armor_buff"))) {
//            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
//                    .addPersistentModifier(water_armor_buff);
//        }
//        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_armor_toughness_buff"))) {
//            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
//                    .addPersistentModifier(water_armor_toughness_buff);
//        }
//        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_movement_buff"))) {
//            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY))
//                    .addPersistentModifier(water_movement_buff);
//        }
//        if (!player.getAttributes().getCustomInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_mining_buff"))) {
//            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED))
//                    .addPersistentModifier(water_speed_buff);
//        }
//        if (!player.getAttributes().getCustomInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_speed_buff"))) {
//            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
//                    .addPersistentModifier(water_mining_buff);
//        }
    }

    @Unique
    private void removeRainBuff(ServerPlayerEntity player) {
//        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
//                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_atk_buff"));
//        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
//                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_atk_speed_buff"));
//        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK))
//                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_kb_buff"));
//        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_LUCK))
//                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_luck_buff"));
//        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
//                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_armor_buff"));
//        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
//                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_armor_toughness_buff"));
//        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
//                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_movement_buff"));
//        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
//                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_mining_buff"));
//        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
//                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_speed_buff"));
    }

    @Unique
    private void applyWaterBuff(ServerPlayerEntity player) {

        EntityAttributeModifier water_atk_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_atk_buff"),
                1.25,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_atk_speed_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_atk_speed_buff"),
                1.25,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_kb_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_kb_buff"),
                1.25,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_luck_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_luck_buff"),
                2,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_movement_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_movement_buff"),
                1,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_mining_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_mining_buff"),
                2,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_armor_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_armor_buff"),
                1.25,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_armor_toughness_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_armor_toughness_buff"),
                1.25,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_speed_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_speed_buff"),
                0.05,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_atk_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                    .addPersistentModifier(water_atk_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_atk_speed_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
                    .addPersistentModifier(water_atk_speed_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_kb_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK))
                    .addPersistentModifier(water_kb_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_LUCK).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_luck_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_LUCK))
                    .addPersistentModifier(water_luck_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_armor_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
                    .addPersistentModifier(water_armor_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_armor_toughness_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                    .addPersistentModifier(water_armor_toughness_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_movement_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY))
                    .addPersistentModifier(water_movement_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_mining_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED))
                    .addPersistentModifier(water_mining_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_speed_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                    .addPersistentModifier(water_speed_buff);
        }
    }


    @Unique
    private void removeWaterBuff(ServerPlayerEntity player) {
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_atk_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_atk_speed_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_kb_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_LUCK))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_luck_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_armor_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_armor_toughness_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_movement_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_mining_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_speed_buff"));
    }

    @Unique
    private boolean isBeingRainedOn(ServerPlayerEntity player) {
        try {
            if (isBeingRainedOnMethod == null) {
                isBeingRainedOnMethod = Entity.class.getDeclaredMethod("isBeingRainedOn");
                isBeingRainedOnMethod.setAccessible(true);
            }
            return (boolean) isBeingRainedOnMethod.invoke(player);
        } catch (Exception e) {
            Weirdcraft.LOGGER.error("Failed to invoke isBeingRainedOn: ", e);
            return false;
        }
    }

    @Unique
    public boolean isWieldingHolyMackerel(ServerPlayerEntity player) {
        boolean hasHolyMackerel = player.getInventory().main.stream()
                .anyMatch(stack -> stack.getItem() == CustomItems.HOLY_MACKEREL) || player.getInventory().offHand.stream()
                .anyMatch(stack -> stack.getItem() == CustomItems.HOLY_MACKEREL);
        return hasHolyMackerel;
    }

    @Unique
    public HolyMackerelItem getHolyMackerel(ServerPlayerEntity player) {
        // Check the main inventory for the Holy Mackerel
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() == CustomItems.HOLY_MACKEREL) {
                return (HolyMackerelItem) stack.getItem(); // Cast to HolyMackerelItem
            }
        }

        // Check the offhand inventory for the Holy Mackerel
        for (ItemStack stack : player.getInventory().offHand) {
            if (stack.getItem() == CustomItems.HOLY_MACKEREL) {
                return (HolyMackerelItem) stack.getItem(); // Cast to HolyMackerelItem
            }
        }

        // Return null if no Holy Mackerel is found
        return null;
    }



}