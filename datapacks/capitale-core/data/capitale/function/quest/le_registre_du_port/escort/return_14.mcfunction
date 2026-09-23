
execute if entity @e[type=marker,tag=wp_leovic_registre_port_n,limit=1] run tag @s add escort_moving
execute if entity @e[type=marker,tag=wp_leovic_registre_port_n,limit=1] run scoreboard players set @s NPC_PATROL_STATE 13
scoreboard players set @s NPC_PATROL_CD 40
