tag @s remove cap_apply_complete
execute if score @s QUEST_SIDE_BA_Q03 matches 50 run tag @s add cap_apply_complete
execute unless entity @s[tag=cap_apply_complete] run tellraw @s {"text":"[Quête] Lysandre n’attend pas encore de conclure cette mission.","color":"red"}
execute if entity @s[tag=cap_apply_complete] run function capitale:quest/dialogue/clear_self
execute if entity @s[tag=cap_apply_complete] run scoreboard players set @s QUEST_SIDE_BA_Q03 100
execute if entity @s[tag=cap_apply_complete] run scoreboard players set @s CAP_QUETEACTIVE 0
execute if entity @s[tag=cap_apply_complete] run scoreboard players add @s REP_BAS_ANNEAUX 5
execute if entity @s[tag=cap_apply_complete] run give @s capitale_currency:martin_dor 60
execute if entity @s[tag=cap_apply_complete] run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 0.55 1.1
execute if entity @s[tag=cap_apply_complete] run title @s title {"text":"Quête terminée","color":"green"}
execute if entity @s[tag=cap_apply_complete] run title @s subtitle {"text":"Le Marché des Mille Voix","color":"yellow"}
execute if entity @s[tag=cap_apply_complete] run tellraw @s [{"text":"[Récompense]","color":"green"},{"text":" +60 Martins, +5 réputation Bas-Anneaux.","color":"white"}]
tag @s remove cap_apply_complete
