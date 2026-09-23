execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_c,limit=1] run tp @s @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_c,limit=1]
execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_c,limit=1] run scoreboard players set @s NPC_PATROL_STATE 2
execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_c,limit=1] run scoreboard players set @s NPC_PATROL_DIR 0
execute unless entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_c,limit=1] if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_a,limit=1] run tp @s @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_a,limit=1]
execute unless entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_c,limit=1] if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_a,limit=1] run scoreboard players set @s NPC_PATROL_STATE 0
execute unless entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_c,limit=1] if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_a,limit=1] run scoreboard players set @s NPC_PATROL_DIR 1
scoreboard players set @s NPC_PATROL_CD 40
