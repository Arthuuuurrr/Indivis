scoreboard players add @s QUEST_SIDE_BA_Q04 0
execute unless score @s QUEST_SIDE_BA_Q04 matches 40 run tellraw @s {"text":"[Quête] Le marchand n’est pas l’étape actuelle.","color":"red"}
execute if score @s QUEST_SIDE_BA_Q04 matches 40 unless entity @e[type=marker,tag=anchor_side_ba_q04_marchand,limit=1] run tellraw @s {"text":"[Setup] Anchor Q04 marchand absent. Place-le avec le menu setup Bas-Anneaux.","color":"red"}
execute at @s if score @s QUEST_SIDE_BA_Q04 matches 40 if entity @e[type=marker,tag=anchor_side_ba_q04_marchand,limit=1] unless entity @e[type=marker,tag=anchor_side_ba_q04_marchand,distance=..8,limit=1] run tellraw @s {"text":"[Quête] Vous devez être auprès du marchand inquiet. Le point est probablement mal placé.","color":"red"}
execute at @s if score @s QUEST_SIDE_BA_Q04 matches 40 if entity @e[type=marker,tag=anchor_side_ba_q04_marchand,distance=..8,limit=1] run function capitale:quest/side/bas_anneaux/q04_patrouille/step_marchand_apply_self
