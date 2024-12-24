package com.maaackz.weirdcraft.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class CustomRegistryDataGenerator extends FabricDynamicRegistryProvider {
    public CustomRegistryDataGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
//        entries.addAll(registries.getWrapperOrThrow(RegistryKeys.TRIM_MATERIAL));
//        entries.addAll(registries.getWrapperOrThrow(RegistryKeys.TRIM_PATTERN));
//        entries.addAll(registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT));

        entries.addAll(registries.getWrapperOrThrow(RegistryKeys.CONFIGURED_FEATURE));
        entries.addAll(registries.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE));
    }

    @Override
    public String getName() {
        return "";
    }
}