function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SIDE_BA_Q03 0
scoreboard players add @s QUEST_SIDE_BA_Q01 0
tag @s remove cap_npc_route_q03
execute if score @s QUEST_SIDE_BA_Q03 matches 40 if score @s QUEST_SIDE_BA_Q01 matches 101 run tag @s add cap_npc_route_q03
execute if entity @s[tag=cap_npc_route_q03] run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/delivery_mira_npc_self
execute unless entity @s[tag=cap_npc_route_q03] run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/interact_self
tag @s remove cap_npc_route_q03
