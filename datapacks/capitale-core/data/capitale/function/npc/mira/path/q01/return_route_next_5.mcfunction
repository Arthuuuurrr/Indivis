# Retour détaillé Mira Q01 -> r05 puis suite inverse
execute if entity @e[type=marker,tag=wp_mira_q01_r05,limit=1] run tp @s @e[type=marker,tag=wp_mira_q01_r05,limit=1]
execute if entity @e[type=marker,tag=wp_mira_q01_r05,limit=1] run scoreboard players set @s NPC_PATROL_STATE 4
execute unless entity @e[type=marker,tag=wp_mira_q01_r05,limit=1] run function capitale:npc/mira/path/q01/return_route_next_4
execute if entity @e[type=marker,tag=wp_mira_q01_r05,limit=1] run scoreboard players set @s NPC_PATROL_CD 6
