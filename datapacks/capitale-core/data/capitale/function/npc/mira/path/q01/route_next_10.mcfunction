# Route détaillée Mira Q01 -> point 11
execute if entity @e[type=marker,tag=wp_mira_q01_r11,limit=1] run tp @s @e[type=marker,tag=wp_mira_q01_r11,limit=1]
execute if entity @e[type=marker,tag=wp_mira_q01_r11,limit=1] run scoreboard players set @s NPC_PATROL_STATE 11
execute unless entity @e[type=marker,tag=wp_mira_q01_r11,limit=1] if entity @e[type=marker,tag=wp_mira_q01_cache,limit=1] run tp @s @e[type=marker,tag=wp_mira_q01_cache,limit=1]
execute unless entity @e[type=marker,tag=wp_mira_q01_r11,limit=1] if entity @e[type=marker,tag=wp_mira_q01_cache,limit=1] run scoreboard players set @s NPC_PATROL_STATE 13
execute unless entity @e[type=marker,tag=wp_mira_q01_r11,limit=1] unless entity @e[type=marker,tag=wp_mira_q01_cache,limit=1] run function capitale:npc/mira/path/q01/return_home
scoreboard players set @s NPC_PATROL_CD 6
