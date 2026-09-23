execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_14_f,limit=1] run tp @s @e[type=marker,tag=wp_gardeprofondeurs_patrol_14_f,limit=1]
execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_14_f,limit=1] run scoreboard players set @s NPC_PATROL_STATE 5
execute unless entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_14_f,limit=1] if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_14_a,limit=1] run tp @s @e[type=marker,tag=wp_gardeprofondeurs_patrol_14_a,limit=1]
execute unless entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_14_f,limit=1] run scoreboard players set @s NPC_PATROL_STATE 0
scoreboard players set @s NPC_PATROL_CD 40
