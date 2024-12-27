package com.maaackz.weirdcraft.datagen;

import com.maaackz.weirdcraft.Weirdcraft;
import com.maaackz.weirdcraft.entity.HolyMackerelEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CustomEntities {
    public static final EntityType<HolyMackerelEntity> HOLY_MACKEREL = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Weirdcraft.MOD_ID, "holy_mackerel"),
            EntityType.Builder.create(HolyMackerelEntity::new,SpawnGroup.WATER_CREATURE)
                    .dimensions(0.6F, 0.4F)//.allowSpawningInside()
                    .build()
    );

    public static void registerEntities() {
        Weirdcraft.LOGGER.info("Registering entities...");
    }
}