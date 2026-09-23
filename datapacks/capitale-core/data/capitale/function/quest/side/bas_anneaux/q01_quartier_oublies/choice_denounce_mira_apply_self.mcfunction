tag @s remove cap_apply_choice
execute if score @s QUEST_SIDE_BA_Q01 matches 30 run tag @s add cap_apply_choice
execute unless entity @s[tag=cap_apply_choice] run tellraw @s {"text":"[Quête] Ce choix n’est plus disponible.","color":"red"}
execute if entity @s[tag=cap_apply_choice] run scoreboard players set @s QUEST_SIDE_BA_Q01 102
execute if entity @s[tag=cap_apply_choice] run scoreboard players set @s CAP_QUETEACTIVE 0
execute if entity @s[tag=cap_apply_choice] run function capitale:quest/dialogue/clear_self
execute if entity @s[tag=cap_apply_choice] run scoreboard players add @s REP_BA_GARDES 4
execute if entity @s[tag=cap_apply_choice] run give @s capitale_currency:martin_dor 60
execute if entity @s[tag=cap_apply_choice] run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 0.55 1.0
execute if entity @s[tag=cap_apply_choice] run title @s title {"text":"Quête terminée","color":"green"}
execute if entity @s[tag=cap_apply_choice] run title @s subtitle {"text":"Le Quartier des Oubliés — Mira dénoncée","color":"yellow"}
execute if entity @s[tag=cap_apply_choice] run tellraw @s [{"text":"[Mira]","color":"aqua"},{"text":" : Très bien. Au moins je sais à qui je parle maintenant.","color":"white"}]
execute if entity @s[tag=cap_apply_choice] run tellraw @s [{"text":"[Récompense]","color":"green"},{"text":" +60 Martins, +4 réputation gardes des Bas-Anneaux.","color":"white"}]
tag @s remove cap_apply_choice
