function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_ASCENSION matches 50 run scoreboard players set @s QUEST_VN_ASCENSION 60
execute if score @s QUEST_VN_ASCENSION matches 60 run title @s times 10 70 20
execute if score @s QUEST_VN_ASCENSION matches 60 run title @s title {"text":"L'Ascension","color":"gold","bold":true}
execute if score @s QUEST_VN_ASCENSION matches 60 run title @s subtitle {"text":"Le Vent Noir attend au-dessus des docks.","color":"white"}
execute if score @s QUEST_VN_ASCENSION matches 60 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_ASCENSION matches 60 run tellraw @s {"text":"[Quête] L'ascension commence. Terminez la séquence de départ lorsque la scène est jouée.","color":"gold"}
execute if score @s QUEST_VN_ASCENSION matches 60 at @s run playsound minecraft:block.beacon.activate master @s ~ ~ ~ 0.8 0.72
