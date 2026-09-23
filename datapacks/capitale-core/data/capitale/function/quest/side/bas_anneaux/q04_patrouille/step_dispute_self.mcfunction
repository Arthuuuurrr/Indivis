scoreboard players add @s QUEST_SIDE_BA_Q04 0
execute unless score @s QUEST_SIDE_BA_Q04 matches 30 run tellraw @s {"text":"[Quête] La dispute n’est pas l’étape actuelle.","color":"red"}
execute if score @s QUEST_SIDE_BA_Q04 matches 30 unless entity @e[type=marker,tag=anchor_side_ba_q04_dispute,limit=1] run tellraw @s {"text":"[Setup] Anchor Q04 dispute absent. Place-le avec le menu setup Bas-Anneaux.","color":"red"}
execute at @s if score @s QUEST_SIDE_BA_Q04 matches 30 if entity @e[type=marker,tag=anchor_side_ba_q04_dispute,limit=1] unless entity @e[type=marker,tag=anchor_side_ba_q04_dispute,distance=..8,limit=1] run tellraw @s {"text":"[Quête] Vous devez être auprès de la dispute de quartier. Le point est probablement mal placé.","color":"red"}
execute at @s if score @s QUEST_SIDE_BA_Q04 matches 30 if entity @e[type=marker,tag=anchor_side_ba_q04_dispute,distance=..8,limit=1] run function capitale:quest/side/bas_anneaux/q04_patrouille/step_dispute_apply_self
