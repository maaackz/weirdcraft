package com.maaackz.weirdcraft.structure;

import com.google.common.collect.ImmutableList;
import com.maaackz.weirdcraft.Weirdcraft;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;

public class CustomStructurePools {

    public static final RegistryKey<StructurePool> PYRAMID = registerKey("pyramid/start_pool");

    public static void bootstrap(Registerable<StructurePool> context) {
        RegistryEntryLookup<StructurePool> poolRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
        RegistryEntry.Reference<StructurePool> emptyPool = poolRegistryEntryLookup.getOrThrow(StructurePools.EMPTY);
        RegistryEntryLookup<StructureProcessorList> processorListRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);

        // Ensure the path to your structure is correct: "pyramid/pyramid"
        register(context, PYRAMID, new StructurePool(
                emptyPool,
                ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle(
                        Identifier.of(Weirdcraft.MOD_ID, "pyramid/pyramid").toString(),
                        processorListRegistryEntryLookup.getOrThrow(CustomStructureProcessorLists.PYRAMID_PROCESSOR)), 1)),
                StructurePool.Projection.RIGID
        ));
    }

    private static RegistryKey<StructurePool> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.TEMPLATE_POOL, Identifier.of(Weirdcraft.MOD_ID, name));
    }

    private static void register(Registerable<StructurePool> context, RegistryKey<StructurePool> key, StructurePool structurePool) {
        context.register(key, structurePool);
    }
}
