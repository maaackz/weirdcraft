
package com.maaackz.weirdcraft;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class EMICompat implements EmiPlugin {

    @Override
    @SuppressWarnings("unchecked")
    public void register(EmiRegistry registry){
        // Remove EMI stacks for items in the "weirdcraft" namespace
        registry.removeEmiStacks(stack -> {
            // Check if the stack's namespace matches "weirdcraft"
            if (stack.getId().getNamespace().equals("weirdcraft")) {
//                System.out.println("REMOVED");
                return true; // This removes the stack from EMI's registry
            }
            return false; // Keep other items in the registry
        });
    }

}
