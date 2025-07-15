package com.maaackz.weirdcraft;

import com.maaackz.weirdcraft.block.CustomBlocks;
import com.maaackz.weirdcraft.datagen.CustomEntities;
import com.maaackz.weirdcraft.datagen.CustomEntityAttributes;
import com.maaackz.weirdcraft.entity.HolyMackerelEntity;
import com.maaackz.weirdcraft.item.CustomItemGroups;
import com.maaackz.weirdcraft.item.CustomItems;
import com.maaackz.weirdcraft.item.custom.DreamcastHelmetItem;
import com.maaackz.weirdcraft.item.custom.PocketWatchItem;
import com.maaackz.weirdcraft.network.*;
import com.maaackz.weirdcraft.sound.CustomSounds;
import com.maaackz.weirdcraft.util.CustomLootTableModifiers;
import com.maaackz.weirdcraft.world.gen.CustomWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

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
		CustomEntities.registerEntities();
		CustomEntityAttributes.registerAttributes();
//		CustomOreGeneration.generateOres();

		CustomLootTableModifiers.modifyLootTables();

		CustomWorldGeneration.generateModWorldGen();
		FabricDefaultAttributeRegistry.register(CustomEntities.HOLY_MACKEREL, HolyMackerelEntity.createAttributes());

		// Register the TimePayload codec
		PayloadTypeRegistry.playC2S().register(TimePayload.ID, TimePayload.CODEC);

		final Map<ServerPlayerEntity, Boolean> activePlayers = new ConcurrentHashMap<>();
		final Map<ServerPlayerEntity, Boolean> lastActionMap = new ConcurrentHashMap<>();

//		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
//			if (!world.isClient && entity instanceof HolyMackerelEntity) {
//				System.out.println("Holy Mackerel right clicked");
//				ItemStack fishItemStack = new ItemStack(CustomItems.HOLY_MACKEREL);
//				fishItemStack.set(DataComponentTypes.CUSTOM_NAME, Text.of("Rei"));
//				player.getInventory().insertStack(fishItemStack);
//				player.giveItemStack(fishItemStack);
//				ServerPlayNetworking.send((ServerPlayerEntity) player,new RequestMackerelPayload(true));
//				return ActionResult.SUCCESS;
//			}

//			return ActionResult.PASS;
//		});

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

		// DREAMCAST SLEEP SPECTATE STUFF
		PayloadTypeRegistry.playS2C().register(SleepPayload.ID, SleepPayload.CODEC);
// In your common initializer method
		PayloadTypeRegistry.playC2S().register(EntityRequestPayload.ID, EntityRequestPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(EntityResponsePayload.ID, EntityResponsePayload.CODEC);

		EntitySleepEvents.START_SLEEPING.register((player, pos) -> {
			if (player instanceof ServerPlayerEntity serverPlayer &&
					serverPlayer.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof DreamcastHelmetItem) {

				ServerPlayNetworking.send(serverPlayer, new SleepPayload(true));

			}
		});

		EntitySleepEvents.STOP_SLEEPING.register((player, sleepingPos) -> {
			if (player instanceof ServerPlayerEntity serverPlayer) {
				if (serverPlayer.getCameraEntity() != serverPlayer) {
					ServerPlayNetworking.send(serverPlayer, new SleepPayload(false));
				}
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(EntityRequestPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				System.out.println("Entity request received.");
				ServerPlayerEntity player = context.player();

				DreamcastingServer.handleEntityRequest(player);

			});
		});

		// Raine's Cloud stuff
		PayloadTypeRegistry.playC2S().register(WeatherPayload.ID, WeatherPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(WeatherPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				ServerPlayerEntity player = context.player();

				if (payload.weather() == 1) {
					// Clear weather

					player.getServerWorld().resetWeather();
				} else if (payload.weather() == 2) {
					// Set rain with a random duration (12,000 to 18,000 ticks)
					int rainDuration = ThreadLocalRandom.current().nextInt(12000, 18001);
					player.getServerWorld().setWeather(0, rainDuration, true, false);

				} else if (payload.weather() == 3) {
					// Set thunderstorm with a random duration (3,000 to 9,000 ticks)
					int thunderDuration = ThreadLocalRandom.current().nextInt(3000, 9001);
					player.getServerWorld().setWeather(0, thunderDuration, true, true);
				}
			});
		});

//		PayloadTypeRegistry.playC2S().register(RequestMackerelPayload.ID, RequestMackerelPayload.CODEC);
//		ServerPlayNetworking.registerGlobalReceiver(RequestMackerelPayload.ID, (payload, context) -> {

//			context.server().execute(() -> {
//				ServerPlayerEntity player = context.server().getPlayerManager().getPlayerList().getFirst();
//				ItemStack fishItemStack = new ItemStack(CustomItems.HOLY_MACKEREL);
//				fishItemStack.set(DataComponentTypes.CUSTOM_NAME, Text.of("Rei"));
//				player.getInventory().insertStack(fishItemStack);
//				player.giveItemStack(fishItemStack);
//				ServerPlayNetworking.send(player,new RequestMackerelPayload(true));
//			});
//
//		});


		// Check if EMI is loaded and register exclusions
		if (FabricLoader.getInstance().isModLoaded("emi")) {
			// EMI-specific logic here
		}

		// Register debug commands for chunk loading and dreamcasting
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(net.minecraft.server.command.CommandManager.literal("testchunk")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> {
					ServerCommandSource source = context.getSource();
					if (source.getPlayer() != null) {
						// Test chunk loading at player's position
						DreamcastingServer.debugChunkLoading(source.getPlayer(), source.getPlayer().getBlockPos());
						source.sendMessage(Text.literal("Chunk loading test completed. Check server logs."));
					} else {
						source.sendMessage(Text.literal("This command can only be used by players."));
					}
					return 1;
				})
			);
			
			dispatcher.register(net.minecraft.server.command.CommandManager.literal("dreamcast")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> {
					ServerCommandSource source = context.getSource();
					if (source.getPlayer() != null) {
						// Trigger dreamcasting for the player
						DreamcastingServer.handleEntityRequest(source.getPlayer());
						source.sendMessage(Text.literal("Dreamcasting triggered. Check server logs."));
					} else {
						source.sendMessage(Text.literal("This command can only be used by players."));
					}
					return 1;
				})
			);
		});

		LOGGER.info("weirdcraft mod initialized!");
	}
//
//	public void giveMackerel(ServerPlayerEntity player) {
//		ServerPlayerEntity player = context.player();
////				ItemStack fishItemStack = new ItemStack(CustomItems.HOLY_MACKEREL);
////				fishItemStack.set(DataComponentTypes.CUSTOM_NAME, Text.of("Rei"));
////				player.getInventory().insertStack(fishItemStack);
////				ServerPlayNetworking.send(player,new RequestMackerelPayload(true));
//	}

}
