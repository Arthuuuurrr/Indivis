tag @s remove cap_apply_complete
execute if score @s QUEST_SIDE_BA_Q02 matches 40 run tag @s add cap_apply_complete
execute unless entity @s[tag=cap_apply_complete] run tellraw @s {"text":"[Quête] Elias n’attend pas encore de conclure cette mission.","color":"red"}
execute if entity @s[tag=cap_apply_complete] run function capitale:quest/dialogue/clear_self
execute if entity @s[tag=cap_apply_complete] run scoreboard players set @s QUEST_SIDE_BA_Q02 100
execute if entity @s[tag=cap_apply_complete] run scoreboard players set @s CAP_QUETEACTIVE 0
execute if entity @s[tag=cap_apply_complete] run scoreboard players add @s REP_BA_ARTISANS 8
execute if entity @s[tag=cap_apply_complete] run give @s capitale_currency:martin_dor 50
execute if entity @s[tag=cap_apply_complete] run give @s minecraft:iron_nugget[minecraft:custom_name='{"text":"Pièce d’acier brut","color":"gray","italic":false}',minecraft:lore=['{"text":"Première pièce forgée sous le regard d’Elias Ferbois.","color":"dark_gray","italic":false}']] 1
execute if entity @s[tag=cap_apply_complete] run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 0.55 1.1
execute if entity @s[tag=cap_apply_complete] run title @s title {"text":"Quête terminée","color":"green"}
execute if entity @s[tag=cap_apply_complete] run title @s subtitle {"text":"Le Marteau d’Elias","color":"yellow"}
execute if entity @s[tag=cap_apply_complete] run tellraw @s [{"text":"[Récompense]","color":"green"},{"text":" +50 Martins, +8 réputation artisans des Bas-Anneaux.","color":"white"}]
tag @s remove cap_apply_complete
