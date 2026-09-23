
# Depuis H : si H− existe, retour par ce point ; sinon directement vers G.
execute if entity @e[type=marker,tag=wp_leovic_registre_port_h_before,limit=1] run tag @s add escort_moving
execute if entity @e[type=marker,tag=wp_leovic_registre_port_h_before,limit=1] run scoreboard players set @s NPC_PATROL_STATE 60
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_h_before,limit=1] if entity @e[type=marker,tag=wp_leovic_registre_port_g,limit=1] run tag @s add escort_moving
execute unless entity @e[type=marker,tag=wp_leovic_registre_port_h_before,limit=1] if entity @e[type=marker,tag=wp_leovic_registre_port_g,limit=1] run scoreboard players set @s NPC_PATROL_STATE 6
scoreboard players set @s NPC_PATROL_CD 40
