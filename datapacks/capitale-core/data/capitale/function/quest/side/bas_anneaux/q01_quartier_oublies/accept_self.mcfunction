function capitale:quest/dialogue/clear_self
scoreboard players add @s QUEST_SIDE_BA_Q01 0
execute unless score @s QUEST_SIDE_BA_Q01 matches 0 run tellraw @s {"text":"[Quête] Cette scène n’est plus disponible.","color":"red"}
execute if score @s QUEST_SIDE_BA_Q01 matches 0 run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/start_collision_apply_self
