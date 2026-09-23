scoreboard players add @s QUEST_SIDE_BA_Q03 0
scoreboard players add @s QUEST_SIDE_BA_Q01 0
execute unless score @s QUEST_SIDE_BA_Q03 matches 40 run tellraw @s {"text":"[Quête] Cette livraison finale n’est pas attendue maintenant.","color":"red"}
execute if score @s QUEST_SIDE_BA_Q03 matches 40 unless score @s QUEST_SIDE_BA_Q01 matches 101 run tellraw @s {"text":"[Quête] Cette livraison n’est destinée à Mira que si elle a été aidée dans le Quartier des Oubliés.","color":"red"}
execute if score @s QUEST_SIDE_BA_Q03 matches 40 if score @s QUEST_SIDE_BA_Q01 matches 101 unless entity @e[type=marker,tag=anchor_side_ba_q03_mira,limit=1] run tellraw @s {"text":"[Setup] Anchor Q03 Mira absent. Place-le sur Mira.","color":"red"}
execute at @s if score @s QUEST_SIDE_BA_Q03 matches 40 if score @s QUEST_SIDE_BA_Q01 matches 101 if entity @e[type=marker,tag=anchor_side_ba_q03_mira,limit=1] unless entity @e[type=marker,tag=anchor_side_ba_q03_mira,distance=..8,limit=1] run tellraw @s {"text":"[Quête] Vous devez être auprès de Mira pour cette livraison. Le point Q03 Mira est probablement mal placé.","color":"red"}
execute at @s if score @s QUEST_SIDE_BA_Q03 matches 40 if score @s QUEST_SIDE_BA_Q01 matches 101 if entity @e[type=marker,tag=anchor_side_ba_q03_mira,distance=..8,limit=1] run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/delivery_mira_apply_self
