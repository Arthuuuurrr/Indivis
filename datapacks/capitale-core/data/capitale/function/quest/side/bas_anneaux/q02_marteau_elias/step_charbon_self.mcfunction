scoreboard players add @s QUEST_SIDE_BA_Q02 0
execute unless score @s QUEST_SIDE_BA_Q02 matches 30 run tellraw @s {"text":"[Quête] Le charbon n’est pas attendu maintenant.","color":"red"}
execute if score @s QUEST_SIDE_BA_Q02 matches 30 unless entity @e[type=marker,tag=anchor_side_ba_q02_charbon,limit=1] run tellraw @s {"text":"[Setup] Anchor Q02 charbon absent. Place-le avec le menu setup Bas-Anneaux.","color":"red"}
execute at @s if score @s QUEST_SIDE_BA_Q02 matches 30 if entity @e[type=marker,tag=anchor_side_ba_q02_charbon,limit=1] unless entity @e[type=marker,tag=anchor_side_ba_q02_charbon,distance=..8,limit=1] run tellraw @s {"text":"[Quête] Vous devez être auprès de la réserve de charbon. Utilise la visualisation si le point semble mal placé.","color":"red"}
execute at @s if score @s QUEST_SIDE_BA_Q02 matches 30 if entity @e[type=marker,tag=anchor_side_ba_q02_charbon,distance=..8,limit=1] run function capitale:quest/side/bas_anneaux/q02_marteau_elias/step_charbon_apply_self
