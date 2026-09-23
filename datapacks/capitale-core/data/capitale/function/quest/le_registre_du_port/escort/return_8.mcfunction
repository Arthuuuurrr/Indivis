
# Depuis I : si H+ existe, retour par ce point ; sinon directement vers H.
execute if entity @e[type=marker,tag=wp_leovic_registre_port_h_after,limit=1] run tag @s add escort_moving
execute if entity @e[type=marker,tag=wp_leovic_registre_port_h_after,limit=1] run scoreboard players set @s NPC_PATROL_STATE 61
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_h_after,limit=1] if entity @e[type=marker,tag=wp_leovic_registre_port_h,limit=1] run tag @s add escort_moving
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_h_after,limit=1] if entity @e[type=marker,tag=wp_leovic_registre_port_h,limit=1] run scoreboard players set @s NPC_PATROL_STATE 7
scoreboard players set @s NPC_PATROL_CD 40
