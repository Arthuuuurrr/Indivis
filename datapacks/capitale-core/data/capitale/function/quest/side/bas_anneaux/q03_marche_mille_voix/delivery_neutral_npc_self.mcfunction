scoreboard players add @s QUEST_SIDE_BA_Q03 0
scoreboard players add @s QUEST_SIDE_BA_Q01 0
execute unless score @s QUEST_SIDE_BA_Q03 matches 40 run tellraw @s {"text":"[Habitante] Je ne crois pas attendre quelque chose de Lysandre.","color":"gray"}
execute if score @s QUEST_SIDE_BA_Q03 matches 40 if score @s QUEST_SIDE_BA_Q01 matches 101..102 run tellraw @s {"text":"[Habitante] Ce paquet ne m’est pas destiné. Tes choix ont déjà tracé un autre chemin.","color":"gray"}
execute if score @s QUEST_SIDE_BA_Q03 matches 40 unless score @s QUEST_SIDE_BA_Q01 matches 101..102 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/delivery_neutral_apply_self
