package com.maaackz.weirdcraft.structure;

import com.maaackz.weirdcraft.Weirdcraft;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.structure.Structure;

import java.util.Optional;

public class CustomStructureSets {

    public static final RegistryKey<StructureSet> PYRAMIDS = registerKey("pyramids");

    public static void bootstrap(Registerable<StructureSet> context) {
        RegistryEntryLookup<Structure> structureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.STRUCTURE);
        RegistryEntryLookup<Biome> biomeRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.BIOME);
        RegistryEntryLookup<StructureSet> structureSetLookup = context.getRegistryLookup(RegistryKeys.STRUCTURE_SET);


        register(context, PYRAMIDS, new StructureSet(structureRegistryEntryLookup.getOrThrow(CustomStructures.PYRAMID),
                new RandomSpreadStructurePlacement(
                    new Vec3i(0,20,0),
                    StructurePlacement.FrequencyReductionMethod.DEFAULT,
                    0.2f,
                    1141815106,
                    Optional.empty(),
                    20,
                    5,
                    SpreadType.LINEAR
                )
            )
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static RegistryKey<StructureSet> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.STRUCTURE_SET, Identifier.of(Weirdcraft.MOD_ID, name));
    }

    @SuppressWarnings("SameParameterValue")
    private static void register(Registerable<StructureSet> context, RegistryKey<StructureSet> key, StructureSet structureSet) {
        context.register(key, structureSet);
    }
}