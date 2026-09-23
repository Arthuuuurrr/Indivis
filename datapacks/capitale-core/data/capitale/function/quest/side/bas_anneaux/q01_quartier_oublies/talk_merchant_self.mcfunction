scoreboard players add @s QUEST_SIDE_BA_Q01 0
execute unless score @s QUEST_SIDE_BA_Q01 matches 20 run tellraw @s {"text":"[Marchand] : Je n’ai rien à vous dire pour le moment.","color":"gray"}
execute at @s if score @s QUEST_SIDE_BA_Q01 matches 20 unless entity @e[type=marker,tag=anchor_side_ba_q01_marchand,distance=..8,limit=1] run tellraw @s {"text":"[Quête] Vous devez être auprès du marchand de l’étal.","color":"red"}
execute at @s if score @s QUEST_SIDE_BA_Q01 matches 20 if entity @e[type=marker,tag=anchor_side_ba_q01_marchand,distance=..8,limit=1] run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/talk_merchant_apply_self
