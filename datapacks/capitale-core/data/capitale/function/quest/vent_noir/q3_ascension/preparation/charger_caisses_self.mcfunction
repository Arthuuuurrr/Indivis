function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_ASCENSION matches 30 run scoreboard players set @s QUEST_VN_ASCENSION 40
execute if score @s QUEST_VN_ASCENSION matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_ASCENSION matches 40 run tellraw @s {"text":"[Objectif mis à jour] Les caisses sont chargées. Vérifiez maintenant les moteurs du transport.","color":"yellow"}
execute if score @s QUEST_VN_ASCENSION matches 40 run title @s times 5 50 15
execute if score @s QUEST_VN_ASCENSION matches 40 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s QUEST_VN_ASCENSION matches 40 run title @s subtitle {"text":"Vérifiez les moteurs.","color":"white"}
execute if score @s QUEST_VN_ASCENSION matches 40 at @s run playsound minecraft:block.chest.close master @s ~ ~ ~ 0.7 0.9
