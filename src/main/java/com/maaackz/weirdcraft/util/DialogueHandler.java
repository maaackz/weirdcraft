package com.maaackz.weirdcraft.util;

import com.maaackz.weirdcraft.Weirdcraft;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class DialogueHandler {
    private static final Random RANDOM = new Random();

    private static final Map<String, Object[]> MACKEREL_DIALOGUES = new HashMap<>();
    private static final Map<String, Object[]> NOCTURNE_DIALOGUES = new HashMap<>();

    static {
        // Mackerel Dialogues
        MACKEREL_DIALOGUES.put("meet", new Object[]{
                new Object[]{
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("???: What the!"),
                        5, // Delay after the first message
                        Text.literal("???: Put me back!"),
                        5, // Delay before the next one
                        Text.literal("???: I'm just a normal fish like all the other ones!"),
                        1,
                        Text.literal("???: I just talk, that's all."),
                        5, // Delay before the next one
                        Text.literal("???: Oh wait, you're a fish too...?"),
                        1, // Delay before the next one
                        Text.literal("???: But you can walk!"),
                        3, // Delay before the next one
                        Text.literal("???: ..."),
                        2,
                        Text.literal("???: Actually, I guess I shouldn't even be surprised."),
                        4, // Delay before the next one
                        Text.literal("Rei: We can be friends. Name's Rei.")
                },

        });

        MACKEREL_DIALOGUES.put("meet_non_owner", new Object[]{
                new Object[]{
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("???: Who the §kfuck§r are you?"),
                },
                new Object[]{
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("???: He'll come back for me, you know."),
                },

        });

        MACKEREL_DIALOGUES.put("desert", new Object[][]{
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                     Text.literal("Rei: What the hell are we doing in the desert?")
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: I could've been sipping pink lemonade on the back of Diddy's yacht...")
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Bro forgot to return the slab. LMAO")
                },
        });

        MACKEREL_DIALOGUES.put("nether", new Object[]{
                new Object[]{
                        Map.of("type", "local"), // Properties for this dialogue
                    Text.literal("Rei: God, it’s hot as §kfuck§r in here…")
                },
                new Object[]{
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Dude, just put me in the oven at this point.")
                },
                new Object[]{
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: I wanna go home. What are we even doing here?"),
                        1,
                        Text.literal("Rei: Don't answer that, I actually don't care.")
                },
        });

        MACKEREL_DIALOGUES.put("aether", new Object[]{
                new Object[]{
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: I knew I would go to heaven someday.")
                }
        });
        MACKEREL_DIALOGUES.put("the_end", new Object[]{
                new Object[]{
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Are we on the moon?")
                }
        });

        MACKEREL_DIALOGUES.put("item_frame", new Object[]{
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: I can’t believe you put me on the wall.")
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Yeah, real funny. Now get me down from here.")
                }
        });
        MACKEREL_DIALOGUES.put("player_take_damage", new Object[] {
                new Object[] {
                        Map.of("type", "private"), // Properties for this dialogue
                        Text.literal("Rei: Good going. You know you can hit them back, right?")
                },
                new Object[] {
                        Map.of("type", "private"), // Properties for this dialogue
                        Text.literal("Rei: Dude, fight back.")
                },
                new Object[] {
                        Map.of("type", "private"), // Properties for this dialogue
                        Text.literal("Rei: How did you even make it this far without me?")
                },
                new Object[] {
                        Map.of("type", "private"), // Properties for this dialogue
                        Text.literal("Rei: Yeah, I could take this guy, easy.")
                },
                new Object[] {
                        Map.of("type", "private"), // Properties for this dialogue
                        Text.literal("Rei: You gonna let him hit you like that?")
                },
        });
        MACKEREL_DIALOGUES.put("take_damage", new Object[] {
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Ouch! Ok, ok, I get it!")
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: I think I just saw Jesus.")
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Just because I don't die doesn't mean that doesn't hurt.")
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: I'm calling PETA.")
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: This is animal cruelty.")
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: You have legs and I don't, you're basically hitting a cripple right now.")
                },
                new Object[] {
                        Map.of("type", "private"), // Properties for this dialogue
                        Text.literal("Rei bit you.").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY)
                },
        });
        MACKEREL_DIALOGUES.put("random", new Object[] {
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Don’t you have other friends?")
                },
                new Object[] { // Nested dialogue with delays
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: So you got any friends, or you just talk to fish all day?"),
                        15, // Delay here
                        Text.literal("Rei: You know what, fair point. I call a truce."),
                        15,  // Longer delay here
                        Text.literal("Rei: Truce over."),
                        Text.literal("Rei bit you.").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY)
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Also, I'm a salmon, dumbass.")
                },

                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Can't believe I'm stuck with you now."),
                        2,
                        Text.literal("Rei: Guess it's fine though. Us fish, we gotta stick together y'know."),
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: You know what sounds good right now?"),
                        5,
                        Text.literal("Rei: Sushi."),
                        3,
                        Text.literal("Rei: Just kidding."),
                },
        });

//                new Object[] {
//                        Text.literal("Rei: You know, just because I can breathe on land doesn’t mean I enjoy it.")
//                }, make sure this only plays not in water

        MACKEREL_DIALOGUES.put("rain_buff", new Object[]{
                new Object[] {
                        Map.of("type", "private"), // Properties for this dialogue
                        Text.literal("The rain gives you strength.").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY)
                },
        });

        MACKEREL_DIALOGUES.put("water_buff", new Object[]{
                new Object[] {
                        Map.of("type", "private"), // Properties for this dialogue
                        Text.literal("The water gives you strength.").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY)
                },
        });


        MACKEREL_DIALOGUES.put("buff", new Object[]{
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Finally, some water!")
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Mmm, water, my favorite.")
                },
        });

        MACKEREL_DIALOGUES.put("death", new Object[]{
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Stop letting me die.")
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: It's a good thing I don't die, or I'd be dead.")
                },
        });

        MACKEREL_DIALOGUES.put("owner_death", new Object[]{
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: No, Nathan!")
                },

                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: C'mon, get up man.")
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Don't die on me!")
                },

                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Don't just leave me here!")
                },
        });

        MACKEREL_DIALOGUES.put("non_owner_death", new Object[]{
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        Text.literal("Rei: Good riddance.")
                }
        });

        MACKEREL_DIALOGUES.put("near_non_owner", new Object[]{
                new Object[] {
                        Map.of("type", "private"),
                        Text.literal("§7§oRei whispers to you: I sense someone nearby.").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY)
                },
                new Object[] {
                        Map.of("type", "local"),
                        Text.literal("Rei: Who's that?")
                },
                new Object[] {
                        Map.of("type", "local"), // Properties for this dialogue
                        15,
                        Text.literal("Rei: Tell them to back up."),
                        5,
                        Text.literal("Rei: Oh good, they can hear me."),
                        2,
                        Text.literal("Rei: Back up."),
                },
                new Object[] {
                        Map.of("type", "private"), // Properties for this dialogue
                        30,
                        Text.literal("§7§oRei whispers to you: I don't trust that guy.").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY),
                        1,
                        Text.literal("§7§oRei whispers to you: Gives me the creeps.").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GRAY),
                },
        });




        // Nocturne Dialogues
        NOCTURNE_DIALOGUES.put("meet", new Object[]{
                new Object[] {
                        Text.literal("Do you know what you’re holding?").formatted(Formatting.DARK_PURPLE)
                }
        });

        NOCTURNE_DIALOGUES.put("non_owner_meet", new Object[]{
                new Object[] {
                        Text.literal("Do you know what you’re holding?").formatted(Formatting.DARK_PURPLE)
                }
        });

//        NOCTURNE_DIALOGUES.put("desert", new Text[]{
//                Text.literal("The desert is quiet... too quiet.").formatted(Formatting.DARK_PURPLE)
//        });

//        NOCTURNE_DIALOGUES.put("nether", new Text[]{
//                Text.literal("Even the flames here whisper secrets to me.").formatted(Formatting.DARK_PURPLE)
//        });

        NOCTURNE_DIALOGUES.put("item_frame", new Object[]{
                new Object[] {
                        Text.literal("Displaying me like a trophy? How quaint.").formatted(Formatting.DARK_PURPLE)
                },
                new Object[] {
                        Text.literal("I hope you understand the consequences of this.").formatted(Formatting.DARK_PURPLE)
                }
        });

        NOCTURNE_DIALOGUES.put("random", new Object[]{
                new Object[] {
                        Text.literal("The night holds more answers than you realize.").formatted(Formatting.DARK_PURPLE)
                },
                new Object[] {
                        Text.literal("You’re meddling in forces beyond your understanding.").formatted(Formatting.DARK_PURPLE)
                }
        });

        NOCTURNE_DIALOGUES.put("buff", new Object[]{
                new Object[] {
                        Text.literal("The shadows grow stronger.").formatted(Formatting.DARK_PURPLE)
                }
        });

        NOCTURNE_DIALOGUES.put("death", new Object[]{
                new Object[] {
                        Text.literal("This isn’t the end.").formatted(Formatting.DARK_PURPLE)
                }
        });

        NOCTURNE_DIALOGUES.put("owner_death", new Object[]{
                new Object[] {
                        Text.literal("You were the only one worthy...").formatted(Formatting.DARK_PURPLE)
                }
        });

        NOCTURNE_DIALOGUES.put("non_owner_death", new Object[]{
                new Object[] {
                        Text.literal("Nocturne: Another fool meets their fate.").formatted(Formatting.DARK_PURPLE)
                }
        });
    }
    // Parse dialogue with potential delays and nested objects
    public static List<Object> parseDialogueWithDelays(String character, String type) {
        List<Object> parsedMessages = new ArrayList<>();
        Object[] dialogues = getDialogueForCharacterAndType(character, type);

        if (dialogues != null) {
            for (Object dialogue : dialogues) {
                if (dialogue instanceof Text) {
                    parsedMessages.add(dialogue);  // Regular message
                } else if (dialogue instanceof Object[]) {
                    parsedMessages.addAll(parseDialogueWithDelaysForNested((Object[]) dialogue));  // Handle nested dialogues
                }
            }
        }
        return parsedMessages;
    }

    // Handle nested dialogues or delay numbers
    private static List<Object> parseDialogueWithDelaysForNested(Object[] nestedDialogue) {
        List<Object> parsedNestedMessages = new ArrayList<>();
        for (Object item : nestedDialogue) {
            if (item instanceof Text) {
                parsedNestedMessages.add(item); // Add text message
            } else if (item instanceof Number) {
                parsedNestedMessages.add(item); // Add delay number (e.g., 0.5 or 5)
            }
        }
        return parsedNestedMessages;
    }

    // Retrieve dialogue based on character and type
    private static Object[] getDialogueForCharacterAndType(String character, String type) {
        if (character.equalsIgnoreCase("mackerel")) {
            return MACKEREL_DIALOGUES.get(type);
        }
        return null; // Return null if no dialogues found for the character/type
    }

    public static void sendDialogueWithDelays(ServerPlayerEntity player, String character, String type) {
        List<Object> parsedMessages = parseDialogueWithDelays(character, type);

        if (parsedMessages != null && !parsedMessages.isEmpty()) {
            // Find the dialogue type and pick a single random dialogue set if it's an array
            if (parsedMessages.size() == 1 && parsedMessages.get(0) instanceof Object[]) {
                // If the first element is an Object[] (nested dialogue set), randomly pick one of them
                Object[] selectedDialogueSet = (Object[]) parsedMessages.get(0);
                parsedMessages.clear();
                parsedMessages.addAll(Arrays.asList(selectedDialogueSet)); // Add the selected set's messages
            }

            sendMessagesWithDelays(player, parsedMessages, 0,  player.getServerWorld());
        }
    }

    private static void sendMessagesWithDelays(ServerPlayerEntity player, List<Object> messages, int index, ServerWorld world) {
        if (index >= messages.size()) {
            return; // End if all messages have been sent
        }

        Object currentEntry = messages.get(index);

        // Check if the first entry is a Map representing properties
        Map<?, ?> properties = null;
        if (index == 0 && currentEntry instanceof Map<?, ?>) {
            properties = (Map<?, ?>) currentEntry;
            sendMessagesWithDelays(player, messages, index + 1, world); // Move to the next entry
            return;
        }

        if (currentEntry instanceof Number) {
            // Delay handling
            double delayInSeconds = ((Number) currentEntry).doubleValue();
            long delayInTicks = MathHelper.floor(delayInSeconds * 20);

            // Register a server tick callback to process the delay
            ServerTickEvents.START_SERVER_TICK.register(new ServerTickEvents.StartTick() {
                private long elapsedTicks = 0;
                private boolean hasFinished = false;

                @Override
                public void onStartTick(MinecraftServer server) {
                    if (hasFinished) {
                        return;
                    }

                    elapsedTicks++;

                    if (elapsedTicks >= delayInTicks) {
                        sendMessagesWithDelays(player, messages, index + 1, world);
                        hasFinished = true;
                    }
                }
            });
        } else if (currentEntry instanceof Text) {
            // Message sending
            if (properties != null) {
                String type = (String) properties.get("type");
                if (type != null) {
                    switch (type) {
                        case "public":
                            world.getPlayers().forEach(p -> p.sendMessage((Text) currentEntry, false));
                            break;
                        case "local":
                            world.getPlayers().stream()
                                    .filter(p -> p.squaredDistanceTo(player) <= 16) // Adjust range as needed
                                    .forEach(p -> p.sendMessage((Text) currentEntry, false));
                            break;
                        case "private":
                        default:
                            player.sendMessage((Text) currentEntry, false);
                            break;
                    }
                } else {
                    player.sendMessage((Text) currentEntry, false); // Default to private message
                }
            } else {
                player.sendMessage((Text) currentEntry, false); // No properties, default to private message
            }

            sendMessagesWithDelays(player, messages, index + 1, world);
        } else if (currentEntry instanceof Object[]) {
            // Handle nested dialogues recursively
            sendMessagesWithDelays(player, Arrays.asList((Object[]) currentEntry), 0, world);
        }
    }


    public static void sendDialogue(ServerPlayerEntity player, String character, String type) {
        Map<String, Object[]> dialogues;

        // Select dialogues based on the character
        if (character.equalsIgnoreCase("nocturne")) {
            dialogues = NOCTURNE_DIALOGUES;
        } else if (character.equalsIgnoreCase("mackerel")) {
            dialogues = MACKEREL_DIALOGUES;
        } else {
            // Handle unknown character types (log or use default dialogues)
            dialogues = MACKEREL_DIALOGUES; // Fallback
            Weirdcraft.LOGGER.warn("Unknown character: " + character + ", defaulting to 'mackerel' dialogues.");
        }

        // Get the corresponding messages for the dialogue type
        Object[] messages = dialogues.get(type);
        if (messages != null && messages.length > 0) {
            // Select a random dialogue set
            Object randomMessageOrDialogue = messages[RANDOM.nextInt(messages.length)];

            if (randomMessageOrDialogue instanceof Object[]) {
                // If it's an Object[], handle the sequence (text with delays)
                Object[] nestedMessages = (Object[]) randomMessageOrDialogue;
                sendMessagesWithDelays(player, List.of(nestedMessages), 0, player.getServerWorld());
            } else if (randomMessageOrDialogue instanceof Text) {
                // If it's a single Text, send it directly
                Text message = (Text) randomMessageOrDialogue;
                player.sendMessage(message, false);
            } else {
                Weirdcraft.LOGGER.warn("Selected message is not of type Text or Object[]: " + randomMessageOrDialogue);
            }
        }
    }



    public static void sendRandomDialogue(ServerPlayerEntity player, String character) {
        sendDialogue(player, character, "random");
    }

    public static void sendRandomDialogueOfType(ServerPlayerEntity player, String character, String type) {
        sendDialogue(player, character, type);
    }

    public static boolean isSpecificPlayer(ServerPlayerEntity player, String specificUUID) {
        return player.getUuid().toString().equals(specificUUID);
    }
}
