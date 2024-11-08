package com.maaackz.weirdcraft.structure;

import com.maaackz.weirdcraft.Weirdcraft;
import com.maaackz.weirdcraft.util.CustomTags;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Map;
import java.util.Optional;

public class CustomStructures {

    public static final RegistryKey<Structure> PYRAMID = RegistryKey.of(RegistryKeys.STRUCTURE, Identifier.of(Weirdcraft.MOD_ID, "pyramid"));

    public static void bootstrap(Registerable<Structure> context) {
        RegistryEntryLookup<Biome> registryBiome = context.getRegistryLookup(RegistryKeys.BIOME);

        context.register(
                PYRAMID,
                new Structure(createConfig(registryBiome.getOrThrow(CustomTags.Biomes.PYRAMID_HAS_STRUCTURE))) {
                    @Override
                    protected Optional<StructurePosition> getStructurePosition(Context context) {
                        // Implement structure generation logic here
                        return Optional.empty();
                    }

                    @Override
                    public StructureType<?> getType() {
                        // Return a valid StructureType (replace with your structure type)
                        return StructureType.DESERT_PYRAMID;
                    }
                }
        );
    }

    private static Structure.Config createConfig(RegistryEntryList<Biome> biomes) {
        return createConfig(biomes, Map.of());
    }

    private static Structure.Config createConfig(RegistryEntryList<Biome> biomes, Map<SpawnGroup, StructureSpawns> spawns) {
        return new Structure.Config(biomes, spawns, GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.NONE);
    }
}
