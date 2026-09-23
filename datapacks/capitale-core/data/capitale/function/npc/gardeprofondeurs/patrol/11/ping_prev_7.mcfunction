execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_g,limit=1] run tp @s @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_g,limit=1]
execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_g,limit=1] run scoreboard players set @s NPC_PATROL_STATE 6
execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_g,limit=1] run scoreboard players set @s NPC_PATROL_DIR 1
execute unless entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_g,limit=1] if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_i,limit=1] run tp @s @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_i,limit=1]
execute unless entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_g,limit=1] if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_i,limit=1] run scoreboard players set @s NPC_PATROL_STATE 8
execute unless entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_g,limit=1] if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_11_i,limit=1] run scoreboard players set @s NPC_PATROL_DIR 0
scoreboard players set @s NPC_PATROL_CD 40
