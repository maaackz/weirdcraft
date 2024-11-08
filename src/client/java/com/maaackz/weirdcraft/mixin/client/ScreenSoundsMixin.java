package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.sound.CustomSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(MinecraftClient.class)
public abstract class ScreenSoundsMixin {

	@Shadow @Nullable public Screen currentScreen;
	@Shadow public abstract SoundManager getSoundManager();

	@Inject(at = @At("HEAD"), method = "run")
	private void init(CallbackInfo info) {
		// This code is injected into the start of MinecraftClient.run()V

	}

	@Inject(at = @At("HEAD"), method = "setScreen")
	private void onScreenChanged(Screen screen, CallbackInfo info) {
		if (screen != null ) {
			Text titleText = screen.getTitle();

			// Convert the title text to a string
			String titleString = titleText.toString();

			// Regular expression to extract the key from the format
			Pattern pattern = Pattern.compile("translation\\{key='(.*?)', args=\\[\\]\\}");
			Matcher matcher = pattern.matcher(titleString);

			if (matcher.find()) {
				String key = matcher.group(1); // Extract the key

				// Print the extracted key for debugging
				System.out.println("Extracted Key: " + key);

				// Check if the key is equal to 'modmenu.title'
				if (key.equals("modmenu.title")) {
					// Perform your action here
//					System.out.println("The modmenu.title is active. Performing action...");
					this.getSoundManager().play(PositionedSoundInstance.master(CustomSounds.IT_WAS_MODDED, 1.0F, 0.1F));
				} else if (key.equals("multiplayer.title")) {
					double randomChance = Math.random();
					System.out.println("Random chance to play hate vs luv: " + randomChance);
					// 10% chance to play I_HATE sound
					if (randomChance < 0.05) {
						this.getSoundManager().play(PositionedSoundInstance.master(CustomSounds.BALLSACKS_IN_MY_MOUTH, 1.0F, 0.1F));
					} else if (randomChance < 0.15) {
						this.getSoundManager().play(PositionedSoundInstance.master(CustomSounds.I_HATE, 1.0F, 0.1F));
					} else {
						this.getSoundManager().play(PositionedSoundInstance.master(CustomSounds.I_LUV, 1.0F, 0.1F));
					}
				}
			} else {
				System.out.println("No valid translation key found. No action taken.");
			}
		}
	}
}