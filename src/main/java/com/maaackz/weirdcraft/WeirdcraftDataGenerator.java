package com.maaackz.weirdcraft;

import com.maaackz.weirdcraft.datagen.*;
import com.maaackz.weirdcraft.structure.CustomStructurePools;
import com.maaackz.weirdcraft.structure.CustomStructureProcessorLists;
import com.maaackz.weirdcraft.structure.CustomStructureSets;
import com.maaackz.weirdcraft.structure.CustomStructures;
import com.maaackz.weirdcraft.world.CustomConfiguredFeatures;
import com.maaackz.weirdcraft.world.CustomPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class WeirdcraftDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(CustomBlockTagProvider::new);
		pack.addProvider(CustomItemTagProvider::new);
		pack.addProvider(CustomLootTableProvider::new);
		pack.addProvider(CustomBiomeTagProvider::new);
		pack.addProvider(CustomModelProvider::new);
		pack.addProvider(CustomAdvancementProvider::new);
		pack.addProvider(CustomRegistryDataGenerator::new);
		pack.addProvider(CustomRecipeProvider::new);
//		pack.addProvider(ModPoiTagProvider::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
//		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap);
//		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
//		registryBuilder.addRegistry(RegistryKeys.DAMAGE_TYPE, ModDamageSources::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.STRUCTURE, CustomStructures::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.STRUCTURE_SET, CustomStructureSets::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.TEMPLATE_POOL, CustomStructurePools::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.PROCESSOR_LIST, CustomStructureProcessorLists::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, CustomConfiguredFeatures::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, CustomPlacedFeatures::bootstrap);
	}
}
