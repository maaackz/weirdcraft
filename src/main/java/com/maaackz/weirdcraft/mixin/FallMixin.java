package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.sound.CustomSounds;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class FallMixin {

	private boolean soundPlaying = false;  // Flag to track if the sound is playing
	private int seed;


	@Inject(at = @At("HEAD"), method = "tick")
	private void onTick(CallbackInfo info) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

		if (player.getWorld() == null) return; // Check if the world is valid

		// Check if the player is falling
		if (player.getVelocity().y < -0.5) {  // Adjust this threshold as needed
			// Perform a raycast straight downwards to determine the distance to the ground
			Vec3d startPos = player.getPos();
			Vec3d endPos = startPos.add(0, -7, 0);  // Raycast 5 blocks downwards

			BlockHitResult hitResult = player.getWorld().raycast(new RaycastContext(
					startPos,
					endPos,
					RaycastContext.ShapeType.COLLIDER,
					RaycastContext.FluidHandling.ANY,
					player
			));

			if (hitResult.getType() == HitResult.Type.MISS) {
				// If the sound is not already playing, send the sound packet
				if (!soundPlaying) {
					System.out.println("Playing fall sound AAAAA");

					// Wrap SoundEvent into a RegistryEntry
					RegistryEntry<SoundEvent> aaaaaSoundKey =
							Registries.SOUND_EVENT.getEntry(CustomSounds.AAAAA_SOUND.getId()).orElse(null);

					// Generate a random seed
					seed = player.getRandom().nextInt();

					// Send the PlaySoundFromEntityS2CPacket to play the sound from the player
					for (ServerPlayerEntity serverPlayer : player.server.getPlayerManager().getPlayerList()) {
						serverPlayer.networkHandler.sendPacket(new PlaySoundFromEntityS2CPacket(
								aaaaaSoundKey,            // Sound event to play (replace with your custom sound event key)
								SoundCategory.PLAYERS,    // Sound category (Players in this case)
								player,                   // The entity from which the sound originates (the player)
								0.25F,                    // Volume
								1.0F,                     // Pitch
								seed                      // Seed for the sound instance
						));
					}

					soundPlaying = true;  // Set the flag to indicate the sound is playing
				}
			}
		} else {
			// Reset the flag when the player is no longer falling
			if (soundPlaying) {
				soundPlaying = false;
				for (ServerPlayerEntity serverPlayer : player.server.getPlayerManager().getPlayerList()) {
					serverPlayer.networkHandler.sendPacket(
						new StopSoundS2CPacket(CustomSounds.AAAAA_SOUND.getId(), SoundCategory.PLAYERS)
					);
				}
				// Optionally: handle any additional stopping logic here
			}

		}
	}
}
