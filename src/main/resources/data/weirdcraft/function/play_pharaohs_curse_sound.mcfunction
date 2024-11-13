# Add the tag to the player who triggered the advancement
tag @s add pharaohs_curse_trigger

# Execute the playsound command at the position of the player with the tag, for all players to hear
execute as @a[tag=pharaohs_curse_trigger] at @s run playsound weirdcraft:pharaohs_curse ambient @a ~ ~ ~

# Remove the tag after the sound is played
tag @s remove pharaohs_curse_trigger
