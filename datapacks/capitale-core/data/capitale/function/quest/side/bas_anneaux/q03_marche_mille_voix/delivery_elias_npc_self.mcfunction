scoreboard players add @s QUEST_SIDE_BA_Q03 0
execute unless score @s QUEST_SIDE_BA_Q03 matches 20 run tellraw @s {"text":"[Elias Ferbois] Rien à remettre pour Lysandre pour l’instant.","color":"gray"}
execute if score @s QUEST_SIDE_BA_Q03 matches 20 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/delivery_elias_apply_self
