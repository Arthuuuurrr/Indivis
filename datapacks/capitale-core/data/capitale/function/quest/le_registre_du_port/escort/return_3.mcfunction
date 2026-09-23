execute if entity @e[type=marker,tag=wp_leovic_registre_port_c,limit=1] run tag @s add escort_moving
execute if entity @e[type=marker,tag=wp_leovic_registre_port_c,limit=1] run scoreboard players set @s NPC_PATROL_STATE 2
scoreboard players set @s NPC_PATROL_CD 40
