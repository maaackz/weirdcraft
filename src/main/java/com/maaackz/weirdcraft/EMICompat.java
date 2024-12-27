
package com.maaackz.weirdcraft;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

import java.util.Set;

@EmiEntrypoint
public class EMICompat implements EmiPlugin {

    @Override
    @SuppressWarnings("unchecked")
    public void register(EmiRegistry registry) {
        // Define a set of item IDs to keep
        Set<String> itemsToKeep = Set.of(
                "weirdcraft:weirdium_helmet",
                "weirdcraft:weirdium_chestplate",
                "weirdcraft:weirdium_leggings",
                "weirdcraft:weirdium_boots",
                "weirdcraft:weirdium_ingot",
                "weirdcraft:weirdium_ore",
                "weirdcraft:raw_weirdium",
                "weirdcraft:deepslate_weirdium_ore",
                "weirdcraft:weirdium_block",
                "weirdcraft:raw_weirdium_block",
                "weirdcraft:banana",
                "weirdcraft:golden_banana",
                "weirdcraft:enchanted_golden_banana",
                "weirdcraft:sand_ocean_music_disc",
                "weirdcraft:weirdium_sword",
                "weirdcraft:weirdium_pickaxe",
                "weirdcraft:weirdium_axe",
                "weirdcraft:weirdium_shovel",
                "weirdcraft:weirdium_hoe"


        );

        // Remove EMI stacks for items in the "weirdcraft" namespace unless they are in the itemsToKeep set
        registry.removeEmiStacks(stack -> {
            String itemId = String.valueOf(stack.getId());

            // Check if the stack's namespace matches "weirdcraft"
            if (stack.getId().getNamespace().equals("weirdcraft")) {
                if (itemsToKeep.contains(itemId)) {
                    return false; // Keep this item
                }
                else {
                    System.out.println(itemId);
                    return true; // Remove other items
                }

            }

            return false; // Keep items not in the "weirdcraft" namespace
        });
    }


}
