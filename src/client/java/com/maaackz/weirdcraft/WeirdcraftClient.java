package com.maaackz.weirdcraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class WeirdcraftClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		System.out.println("Client initialized.");
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		if (FabricLoader.getInstance().isModLoaded("modmenu")) {
			// Register your custom logic for Mod Menu here
			registerModMenuSound();
		}

	}

	private void registerModMenuSound() {
		// In your main mod class or another appropriate place

	}

//	private void onScreenOpen(Screen screen) {
//		if (FabricLoader.getInstance().isModLoaded("modmenu")) {
//			if (screen instanceof ModMenuScreen) {
//				// Play your sound effect
//				MinecraftClient.getInstance().getSoundManager().play(SoundEvents.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
//			}
//			MinecraftClient.getInstance().getScreen().addListener(new ScreenListener() {
//				@Override
//				public void onScreenOpened(Screen screen) {
//					onScreenOpen(screen);
//				}
//			});
//		}
//		else
//		{
//
//		}
//	}

// Listen to screen events

}