# bed_destroyed.mcfunction
# Display a chat message to all players with dynamic values

# Setup scoreboards (run once in the initialization)
#scoreboard objectives add bed_name dummy
#scoreboard objectives add bed_color dummy
#scoreboard objectives add player_name dummy


#tellraw @a ["",{"text":"\n"},{"text":"BED DESTRUCTION > ","bold":true},{"score":{"name":"@s","objective":"bed_color"}},{"text":" ", "text":""}, {"text":"was destroyed by ","color":"gray"},{"score":{"name":"@s","objective":"player_name"}},{"text":"!","color":"gray"},{"text":"\n"}]
#title @a times 20 100 20
#title @a title {"text":"BED DESTROYED!","color":"red"}
#title @a subtitle {"text":"You will no longer respawn!"}
