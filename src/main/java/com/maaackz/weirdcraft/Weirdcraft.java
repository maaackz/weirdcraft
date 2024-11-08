package com.maaackz.weirdcraft;

import com.maaackz.weirdcraft.item.CustomItemGroups;
import com.maaackz.weirdcraft.item.CustomItems;
import com.maaackz.weirdcraft.sound.CustomSounds;
import com.maaackz.weirdcraft.util.CustomLootTableModifiers;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Weirdcraft implements ModInitializer {

	public static final String MOD_ID = "weirdcraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("weirdcraft mod initializing...");

		CustomItemGroups.registerItemGroups();
		CustomItemGroups.registerToVanillaItemGroups();

		CustomItems.registerItems();
		CustomSounds.registerSounds();

		CustomLootTableModifiers.modifyLootTables();

		LOGGER.info("weirdcraft mod initialized!");
	}

}