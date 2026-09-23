execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_14_o,limit=1] run tp @s @e[type=marker,tag=wp_gardeprofondeurs_patrol_14_o,limit=1]
execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_14_o,limit=1] run scoreboard players set @s NPC_PATROL_STATE 14
execute if entity @e[type=marker,tag=wp_gardeprofondeurs_patrol_14_o,limit=1] run scoreboard players set @s NPC_PATROL_DIR 1
scoreboard players set @s NPC_PATROL_CD 40
