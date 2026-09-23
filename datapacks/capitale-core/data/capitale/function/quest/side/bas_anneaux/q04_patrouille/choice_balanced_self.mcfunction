tag @s remove cap_apply_choice
execute if score @s QUEST_SIDE_BA_Q04 matches 50 run tag @s add cap_apply_choice
execute unless entity @s[tag=cap_apply_choice] run tellraw @s {"text":"[Quête] Ce choix n’est plus disponible.","color":"red"}
execute if entity @s[tag=cap_apply_choice] run function capitale:quest/dialogue/clear_self
execute if entity @s[tag=cap_apply_choice] run scoreboard players set @s QUEST_SIDE_BA_Q04 103
execute if entity @s[tag=cap_apply_choice] run scoreboard players set @s CAP_QUETEACTIVE 0
execute if entity @s[tag=cap_apply_choice] run scoreboard players add @s REP_BAS_ANNEAUX 4
execute if entity @s[tag=cap_apply_choice] run scoreboard players add @s REP_BA_GARDES 5
execute if entity @s[tag=cap_apply_choice] run give @s capitale_currency:martin_dor 60
execute if entity @s[tag=cap_apply_choice] run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 0.55 1.1
execute if entity @s[tag=cap_apply_choice] run title @s title {"text":"Quête terminée","color":"green"}
execute if entity @s[tag=cap_apply_choice] run title @s subtitle {"text":"La Patrouille — solution équilibrée","color":"yellow"}
execute if entity @s[tag=cap_apply_choice] run tellraw @s [{"text":"[Roland]","color":"aqua"},{"text":" : Tu as rendu ce qui devait l’être et gardé une trace. C’est souvent le mieux que la ville accepte.","color":"white"}]
execute if entity @s[tag=cap_apply_choice] run tellraw @s [{"text":"[Récompense]","color":"green"},{"text":" +60 Martins, +4 Bas-Anneaux, +5 gardes.","color":"white"}]
tag @s remove cap_apply_choice
