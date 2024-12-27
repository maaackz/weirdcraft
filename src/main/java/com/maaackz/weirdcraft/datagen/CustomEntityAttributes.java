package com.maaackz.weirdcraft.datagen;

import com.maaackz.weirdcraft.entity.HolyMackerelEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class CustomEntityAttributes {
    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(CustomEntities.HOLY_MACKEREL, HolyMackerelEntity.createAttributes());
    }
}