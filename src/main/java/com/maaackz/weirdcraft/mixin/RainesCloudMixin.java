package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.Weirdcraft;
import com.maaackz.weirdcraft.item.CustomItems;
import com.maaackz.weirdcraft.util.DialogueHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
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
public abstract class RainesCloudMixin {

    @Unique
    private boolean isRainBuffActive = false;
    @Unique
    private boolean isThunderBuffActive = false;
    @Unique
    private static Method isBeingRainedOnMethod;

    @Unique
    private static final long MESSAGE_COOLDOWN = 60000; // 1 minute cooldown for messages
    @Unique
    private long lastMessageTime = 0; // Timestamp of the last message sent

    // Map to track which players have received the "meet" dialogue (by UUID)
    @Unique
    private static final Map<UUID, Boolean> playerDialogueStatus = new HashMap<>();

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        boolean isRainingOnPlayer = isBeingRainedOn(player);
        boolean isThundering = player.getWorld().isThundering(); // Check if the world is thundering

        // Manage buffs based on rain and thunder states
        handleRainBuff(player, isRainingOnPlayer);
        handleThunderBuff(player, isThundering);

        // Custom logic for Holy Mackerel item
        if (isWieldingRainesCloud(player)) {
            if (!hasReceivedMeetDialogue(player)) {
                lastMessageTime = System.currentTimeMillis(); // Update the last message time
                setHasReceivedMeetDialogue(player); // Mark that the player has received the dialogue
            } else {
                if (canSendDialogue()) {
                    lastMessageTime = System.currentTimeMillis(); // Update the last message time
                }
            }
        } else {
            removeRainBuff(player);
            removeThunderBuff(player);
        }
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            if (source.getAttacker() != null) {
                DialogueHandler.sendDialogue(player, "raine", "player_take_damage");
            }
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void uponDying(CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            DialogueHandler.sendDialogue(player, "raine", "owner_death");
        }
    }

    @Unique
    private boolean canSendDialogue() {
        return System.currentTimeMillis() - lastMessageTime > MESSAGE_COOLDOWN;
    }

    @Unique
    private boolean hasReceivedMeetDialogue(ServerPlayerEntity player) {
        return playerDialogueStatus.getOrDefault(player.getUuid(), false);
    }

    @Unique
    private void setHasReceivedMeetDialogue(ServerPlayerEntity player) {
        playerDialogueStatus.put(player.getUuid(), true);
    }

    @Unique
    private void handleRainBuff(ServerPlayerEntity player, boolean isRaining) {
        if (isRaining && !isRainBuffActive) {
            if (isWieldingRainesCloud(player)) {
                isRainBuffActive = true;
                System.out.println("rain buff");
                applyRainBuff(player); // 20% damage boost in rain
                DialogueHandler.sendDialogue(player, "raine", "rain_buff");
            }
        } else if (!isRaining && isRainBuffActive) {
            removeRainBuff(player);
            isRainBuffActive = false;
        }
    }

    @Unique
    private void handleThunderBuff(ServerPlayerEntity player, boolean isThundering) {
        if (isThundering && !isThunderBuffActive && isWieldingRainesCloud(player)) {
            isThunderBuffActive = true;
            System.out.println("thunder buff");
            applyThunderBuff(player); // 20% damage boost in thunder
            DialogueHandler.sendDialogue(player, "raine", "thunder_buff");
            
        } else if (!isThundering && isThunderBuffActive) {
            removeThunderBuff(player);
            isThunderBuffActive = false;
        }
    }

    @Unique
    private void applyRainBuff(ServerPlayerEntity player) {
        // Attack damage modifier
        EntityAttributeModifier rainAtkBuff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "rain_atk_buff"),
                0.05,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // Attack speed modifier
        EntityAttributeModifier rainAtkSpeedBuff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "rain_atk_speed_buff"),
                0.05,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // Armor modifier
        EntityAttributeModifier rainArmorBuff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "rain_armor_buff"),
                0.05,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // Armor toughness modifier
        EntityAttributeModifier rainArmorToughnessBuff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "rain_armor_toughness_buff"),
                0.05,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // Movement speed modifier
        EntityAttributeModifier rainSpeedBuff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "rain_speed_buff"),
                0.05, // Percent increase (multiplied total)
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "rain_atk_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                    .addTemporaryModifier(rainAtkBuff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "rain_atk_speed_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
                    .addTemporaryModifier(rainAtkSpeedBuff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "rain_armor_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
                    .addTemporaryModifier(rainArmorBuff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "rain_armor_toughness_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                    .addTemporaryModifier(rainArmorToughnessBuff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "rain_speed_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                    .addTemporaryModifier(rainSpeedBuff);
        }
    }

    @Unique
    private void applyThunderBuff(ServerPlayerEntity player) {
        // Attack damage modifier
        EntityAttributeModifier thunderAtkBuff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "thunder_atk_buff"),
                0.15,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // Attack speed modifier
        EntityAttributeModifier thunderAtkSpeedBuff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "thunder_atk_speed_buff"),
                0.15,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // Armor modifier
        EntityAttributeModifier thunderArmorBuff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "thunder_armor_buff"),
                0.15,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // Armor toughness modifier
        EntityAttributeModifier thunderArmorToughnessBuff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "thunder_armor_toughness_buff"),
                0.15,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // Movement speed modifier
        EntityAttributeModifier thunderSpeedBuff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "thunder_speed_buff"),
                0.15, // Percent increase (multiplied total)
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "thunder_atk_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                    .addTemporaryModifier(thunderAtkBuff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "thunder_atk_speed_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
                    .addTemporaryModifier(thunderAtkSpeedBuff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "thunder_armor_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
                    .addTemporaryModifier(thunderArmorBuff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "thunder_armor_toughness_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                    .addTemporaryModifier(thunderArmorToughnessBuff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "thunder_speed_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                    .addTemporaryModifier(thunderSpeedBuff);
        }
    }

    @Unique
    private void removeRainBuff(ServerPlayerEntity player) {
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "rain_atk_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "rain_atk_speed_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "rain_armor_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "rain_armor_toughness_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "rain_speed_buff"));
    }

    @Unique
    private void removeThunderBuff(ServerPlayerEntity player) {
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "thunder_atk_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "thunder_atk_speed_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "thunder_armor_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "thunder_armor_toughness_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "thunder_speed_buff"));
    }

    @Unique
    private void removeBuff(ServerPlayerEntity player, String type) {
        String[] buffs = {
                type + "_atk_buff",
                type + "_atk_speed_buff",
                type + "_armor_buff",
                type + "_armor_toughness_buff",
                type + "_speed_buff"
        };

        for (String buff : buffs) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                    .removeModifier(Identifier.of(Weirdcraft.MOD_ID, buff));
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
                    .removeModifier(Identifier.of(Weirdcraft.MOD_ID, buff));
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
                    .removeModifier(Identifier.of(Weirdcraft.MOD_ID, buff));
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                    .removeModifier(Identifier.of(Weirdcraft.MOD_ID, buff));
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                    .removeModifier(Identifier.of(Weirdcraft.MOD_ID, buff));
        }
    }

    @Unique
    private boolean isBeingRainedOn(ServerPlayerEntity player) {
        try {
            if (isBeingRainedOnMethod == null) {
                isBeingRainedOnMethod = Entity.class.getDeclaredMethod("isBeingRainedOn");
                isBeingRainedOnMethod.setAccessible(true);
            }
//            System.out.println((boolean) isBeingRainedOnMethod.invoke(player));
            return (boolean) isBeingRainedOnMethod.invoke(player);
        } catch (Exception e) {
            Weirdcraft.LOGGER.error("Failed to invoke isBeingRainedOn: ", e);
            return false;
        }
    }

    @Unique
    public boolean isWieldingRainesCloud(ServerPlayerEntity player) {
        return player.getInventory().main.stream()
                .anyMatch(stack -> stack.getItem() == CustomItems.RAINES_CLOUD) || player.getInventory().offHand.stream()
                .anyMatch(stack -> stack.getItem() == CustomItems.RAINES_CLOUD);
    }
}
