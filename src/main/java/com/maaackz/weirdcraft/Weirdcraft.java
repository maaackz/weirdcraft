package com.maaackz.weirdcraft;

import com.maaackz.weirdcraft.block.CustomBlocks;
import com.maaackz.weirdcraft.item.CustomItemGroups;
import com.maaackz.weirdcraft.item.CustomItems;
import com.maaackz.weirdcraft.item.custom.PocketWatchItem;
import com.maaackz.weirdcraft.network.TimePayload;
import com.maaackz.weirdcraft.sound.CustomSounds;
import com.maaackz.weirdcraft.util.CustomLootTableModifiers;
import com.maaackz.weirdcraft.world.gen.CustomWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Weirdcraft implements ModInitializer {

	public static final String MOD_ID = "weirdcraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("weirdcraft mod initializing...");

		CustomItemGroups.registerItemGroups();
		CustomItemGroups.registerToVanillaItemGroups();

		CustomItems.registerItems();
		CustomBlocks.registerBlocks();
		CustomSounds.registerSounds();

		CustomLootTableModifiers.modifyLootTables();

		CustomWorldGeneration.generateModWorldGen();

		// Register the TimePayload codec
		PayloadTypeRegistry.playC2S().register(TimePayload.ID, TimePayload.CODEC);

		final Map<ServerPlayerEntity, Boolean> activePlayers = new ConcurrentHashMap<>();
		final Map<ServerPlayerEntity, Boolean> lastActionMap = new ConcurrentHashMap<>();

		ServerPlayNetworking.registerGlobalReceiver(TimePayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				ServerPlayerEntity player = context.player();

				if (payload.active()) {
					if (payload.advancing()) {
						// Start advancing time
						activePlayers.put(player, true);
					} else {
						// Start reversing time
						activePlayers.put(player, false);
					}
				} else {
					// Stop time adjustment
					activePlayers.remove(player);
					lastActionMap.remove(player); // Reset last action when stopping
				}
			});
		});

		// Register server tick callback during initialization
		ServerTickEvents.START_WORLD_TICK.register(server -> {
			for (Map.Entry<ServerPlayerEntity, Boolean> entry : activePlayers.entrySet()) {
				ServerPlayerEntity player = entry.getKey();
				ServerWorld world = player.getServerWorld();
				Boolean currentAction = entry.getValue();
				Boolean lastAction = lastActionMap.get(player);

				// Only print if the action has changed
				if (!currentAction.equals(lastAction)) {
					if (currentAction) {
						System.out.println("server advancing");
					} else {
						System.out.println("server reversing");
					}
					lastActionMap.put(player, currentAction); // Update last action
				}

				if (currentAction) {
					PocketWatchItem.advanceTime(world);
				} else {
					PocketWatchItem.reverseTime(world);
				}
			}
		});

		// Check if EMI is loaded and register exclusions
		if (FabricLoader.getInstance().isModLoaded("emi")) {
			// EMI-specific logic here
		}

		LOGGER.info("weirdcraft mod initialized!");
	}
}
