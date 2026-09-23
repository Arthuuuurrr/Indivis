scoreboard players add @s QUEST_SIDE_BA_Q03 0
scoreboard players add @s QUEST_SIDE_BA_Q01 0
execute unless score @s QUEST_SIDE_BA_Q03 matches 40 run tellraw @s {"text":"[Mira] J’attends rien de Lysandre, là.","color":"gray"}
execute if score @s QUEST_SIDE_BA_Q03 matches 40 unless score @s QUEST_SIDE_BA_Q01 matches 101 run tellraw @s {"text":"[Mira] Lysandre t’a envoyé vers moi ? Non. Pas après ce qui s’est passé.","color":"gray"}
execute if score @s QUEST_SIDE_BA_Q03 matches 40 if score @s QUEST_SIDE_BA_Q01 matches 101 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/delivery_mira_apply_self
