# BUMP -> RUELLE
execute if entity @e[type=marker,tag=wp_mira_q01_ruelle,limit=1] run tp @s @e[type=marker,tag=wp_mira_q01_ruelle,limit=1]
execute if entity @e[type=marker,tag=wp_mira_q01_ruelle,limit=1] run scoreboard players set @s NPC_PATROL_STATE 2
execute unless entity @e[type=marker,tag=wp_mira_q01_ruelle,limit=1] if entity @e[type=marker,tag=wp_mira_q01_home,limit=1] run tp @s @e[type=marker,tag=wp_mira_q01_home,limit=1]
execute unless entity @e[type=marker,tag=wp_mira_q01_ruelle,limit=1] run scoreboard players set @s NPC_PATROL_STATE 0
scoreboard players set @s NPC_PATROL_CD 8
