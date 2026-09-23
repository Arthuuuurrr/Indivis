scoreboard players add @s QUEST_SIDE_BA_Q04 0
execute unless score @s QUEST_SIDE_BA_Q04 matches 20 run tellraw @s {"text":"[Quête] Ce point n’est pas à inspecter maintenant.","color":"red"}
execute if score @s QUEST_SIDE_BA_Q04 matches 20 unless entity @e[type=marker,tag=anchor_side_ba_q04_inspection,limit=1] run tellraw @s {"text":"[Setup] Anchor Q04 inspection absent. Place-le avec le menu setup Bas-Anneaux.","color":"red"}
execute at @s if score @s QUEST_SIDE_BA_Q04 matches 20 if entity @e[type=marker,tag=anchor_side_ba_q04_inspection,limit=1] unless entity @e[type=marker,tag=anchor_side_ba_q04_inspection,distance=..8,limit=1] run tellraw @s {"text":"[Quête] Vous devez être au point d’inspection de la ronde. Le point est probablement mal placé.","color":"red"}
execute at @s if score @s QUEST_SIDE_BA_Q04 matches 20 if entity @e[type=marker,tag=anchor_side_ba_q04_inspection,distance=..8,limit=1] run function capitale:quest/side/bas_anneaux/q04_patrouille/step_inspection_apply_self
