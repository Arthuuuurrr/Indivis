# Retour détaillé Mira Q01 -> r01 puis suite inverse
execute if entity @e[type=marker,tag=wp_mira_q01_r01,limit=1] run tp @s @e[type=marker,tag=wp_mira_q01_r01,limit=1]
execute if entity @e[type=marker,tag=wp_mira_q01_r01,limit=1] run scoreboard players set @s NPC_PATROL_STATE 0
execute unless entity @e[type=marker,tag=wp_mira_q01_r01,limit=1] run function capitale:npc/mira/path/q01/return_route_next_0
execute if entity @e[type=marker,tag=wp_mira_q01_r01,limit=1] run scoreboard players set @s NPC_PATROL_CD 6
