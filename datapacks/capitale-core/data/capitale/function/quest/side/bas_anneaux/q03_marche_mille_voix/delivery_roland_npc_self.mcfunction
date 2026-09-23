scoreboard players add @s QUEST_SIDE_BA_Q03 0
scoreboard players add @s QUEST_SIDE_BA_Q01 0
execute unless score @s QUEST_SIDE_BA_Q03 matches 40 run tellraw @s {"text":"[Roland] Rien de neuf de Lysandre ? Alors on continue la ronde.","color":"gray"}
execute if score @s QUEST_SIDE_BA_Q03 matches 40 unless score @s QUEST_SIDE_BA_Q01 matches 102 run tellraw @s {"text":"[Roland] Cette note n’est pas pour moi, pas dans ton cas.","color":"gray"}
execute if score @s QUEST_SIDE_BA_Q03 matches 40 if score @s QUEST_SIDE_BA_Q01 matches 102 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/delivery_roland_apply_self
