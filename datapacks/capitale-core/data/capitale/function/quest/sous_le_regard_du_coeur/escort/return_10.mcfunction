execute if entity @e[type=marker,tag=wp_aurele_coeur_j,limit=1] run tag @s add escort_moving
execute if entity @e[type=marker,tag=wp_aurele_coeur_j,limit=1] run scoreboard players set @s NPC_PATROL_STATE 9
scoreboard players set @s NPC_PATROL_CD 40
