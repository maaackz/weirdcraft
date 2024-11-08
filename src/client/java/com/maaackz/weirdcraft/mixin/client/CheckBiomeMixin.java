package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.sound.CustomSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class CheckBiomeMixin  {
	private RegistryEntry<Biome> lastBiome = null;

	// Static method to play sound at the player's location
	private void playLocalSound(SoundEvent soundEvent, PlayerEntity player) {
		if (MinecraftClient.getInstance().world != null) {
			MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().world.playSoundFromEntity(
					player,
					player,
					soundEvent,
					SoundCategory.AMBIENT,
					30.0F,  // Adjust volume as needed
					1.0F    // Adjust pitch as needed
			));
		}
	}

	MinecraftClient client = MinecraftClient.getInstance();
	World world = client.world;
	SoundManager soundManager = client.getSoundManager();

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo info) {


		if (world == null) return; // Check if the world is valid

		PlayerEntity player = (PlayerEntity) (Object) this;

		// Get the current biome the player is in
		RegistryEntry<Biome> biomeEntry = world.getBiome(player.getBlockPos());

		// Check if the biome has changed
		if (biomeEntry != lastBiome) {
			lastBiome = biomeEntry; // Update lastBiome to the current biome

			// Check if the current biome is a desert
			if (biomeEntry.value().equals(world.getRegistryManager().get(RegistryKeys.BIOME).get(Identifier.of("minecraft", "desert")))) {
				System.out.println("Entered desert biome!");

				// Play the Pharaoh's Curse sound from WeirdcraftClient when entering the desert
				playLocalSound(CustomSounds.PHARAOHS_CURSE_SOUND, player);

			}
			else {
				System.out.println("Left desert biome.");
				soundManager.stopSounds(Identifier.of("weirdcraft:pharaohs_curse"), SoundCategory.AMBIENT);
			}
		}
	}

}
