package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.sound.CustomSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class HoldingSlabMixin {

	private ItemStack previousMainHandStack = ItemStack.EMPTY;
	private boolean soundPlaying = false;  // Flag to track if the sound is playing

	@Inject(at = @At("HEAD"), method = "tick")
	private void onTick(CallbackInfo info) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

		int currentSlot = player.getInventory().selectedSlot;
		ItemStack currentMainHandStack = player.getInventory().getMainHandStack();

		// Check if the main hand stack has changed
		if (!ItemStack.areEqual(previousMainHandStack, currentMainHandStack)) {
			previousMainHandStack = currentMainHandStack; // Update previous stack

			// Check if the item stack name contains "sand" and "slab"
			if (currentMainHandStack.getName().getString().toLowerCase().contains("sand") &&
					currentMainHandStack.getName().getString().toLowerCase().contains("slab")) {

				// If sound is not playing, send the play sound packet
				if (!soundPlaying) {
					System.out.println("RETURN THE SLAB");

					// Wrap SoundEvent into a RegistryEntry
					RegistryEntry<SoundEvent> PHARAOHS_CURSE_KEY =
							Registries.SOUND_EVENT.getEntry(CustomSounds.PHARAOHS_CURSE_SOUND.getId()).orElse(null);

					// Generate a random seed
					int seed = player.getRandom().nextInt();

					// Send the PlaySoundFromEntityS2CPacket to play the sound from the player
					player.networkHandler.sendPacket(new PlaySoundFromEntityS2CPacket(
							PHARAOHS_CURSE_KEY,      // Sound event to play (replace with your custom sound event key)
							SoundCategory.PLAYERS,   // Sound category (Players in this case)
							player,                  // The entity from which the sound originates (the player)
							1.0F,                    // Volume
							1.0F,                    // Pitch
							seed                     // Seed for the sound instance
					));
					soundPlaying = true; // Set the flag
				}
			} else {
				// If the stack doesn't match and sound is playing, stop the sound
				if (soundPlaying) {
					// You can't directly stop a sound with `PlaySoundFromEntityS2CPacket`,
					// but stopping logic would involve controlling the flow of your sound system
					soundPlaying = false; // Reset the flag
				}
			}
		}
	}
}
