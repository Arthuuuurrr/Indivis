function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_NAVIRE matches 20 run scoreboard players set @s QUEST_VN_NAVIRE 30
execute if score @s QUEST_VN_NAVIRE matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_NAVIRE matches 30 run tellraw @s {"text":"[Objectif mis à jour] Vous êtes à bord du Vent Noir. Frayez-vous un passage jusqu’à Rask la Balafre.","color":"yellow"}
execute if score @s QUEST_VN_NAVIRE matches 30 run title @s times 5 50 15
execute if score @s QUEST_VN_NAVIRE matches 30 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s QUEST_VN_NAVIRE matches 30 run title @s subtitle {"text":"Atteignez Rask la Balafre.","color":"white"}
execute if score @s QUEST_VN_NAVIRE matches 30 at @s run playsound minecraft:entity.player.attack.sweep master @s ~ ~ ~ 0.6 0.8
