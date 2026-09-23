function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_ASCENSION matches 40 run scoreboard players set @s QUEST_VN_ASCENSION 50
execute if score @s QUEST_VN_ASCENSION matches 50 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_ASCENSION matches 50 run tellraw @s {"text":"[Objectif mis à jour] Les moteurs répondent. Donnez le signal du départ.","color":"yellow"}
execute if score @s QUEST_VN_ASCENSION matches 50 run title @s times 5 50 15
execute if score @s QUEST_VN_ASCENSION matches 50 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s QUEST_VN_ASCENSION matches 50 run title @s subtitle {"text":"Lancez l’ascension.","color":"white"}
execute if score @s QUEST_VN_ASCENSION matches 50 at @s run playsound minecraft:block.beacon.activate master @s ~ ~ ~ 0.6 0.9
