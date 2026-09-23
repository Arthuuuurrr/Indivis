scoreboard players add @s QUEST_SIDE_BA_Q03 0
execute unless score @s QUEST_SIDE_BA_Q03 matches 30 run tellraw @s {"text":"[Greffier] Je n’attends aucun registre de Lysandre pour le moment.","color":"gray"}
execute if score @s QUEST_SIDE_BA_Q03 matches 30 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/delivery_greffier_apply_self
