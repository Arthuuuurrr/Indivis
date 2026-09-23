execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_04_m,limit=1] run tp @s @e[type=marker,tag=wp_gardeprofondeurs_patrol_04_m,limit=1]
execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_04_m,limit=1] run scoreboard players set @s NPC_PATROL_STATE 12
execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_04_m,limit=1] run scoreboard players set @s NPC_PATROL_DIR 0
execute unless entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_04_m,limit=1] if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_04_k,limit=1] run tp @s @e[type=marker,tag=wp_gardeprofondeurs_patrol_04_k,limit=1]
execute unless entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_04_m,limit=1] if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_04_k,limit=1] run scoreboard players set @s NPC_PATROL_STATE 10
execute unless entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_04_m,limit=1] if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_04_k,limit=1] run scoreboard players set @s NPC_PATROL_DIR 1
scoreboard players set @s NPC_PATROL_CD 40
