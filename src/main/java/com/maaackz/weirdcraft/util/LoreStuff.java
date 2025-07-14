package com.maaackz.weirdcraft.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public class LoreStuff {

    public List<Text> nocturne = List.of(new MutableText[]{
            Text.literal("§7§oNocturne whispers to you: Tonight, I grant you my strength."),
            Text.literal("§7§oNocturne whispers to you: You feel a surge of strength."),
            Text.literal("§7§oNocturne whispers to you: You are in weakness."),
            Text.literal("§7§oNocturne whispers to you: In the day, you are nothing."),
            Text.literal("§7§oNocturne whispers to you: You reject me? Pathetic."),
            Text.literal("§7§oNocturne whispers to you: You are weak."),

    });

    public List<Text> mackerel = List.of(new MutableText[]{
            Text.literal("§7§o{name}: stuff."),


    });

    public void sendMessageToPlayer() {

    }

    public void sendMessageToAllPlayers() {

    }

    public void sendMessageToNearbyPlayers() {

    }

}
