tag @e[type=armor_stand,tag=guide_leovic_registre_port] remove escort_moving

tag @e[type=armor_stand,tag=guide_leovic_registre_port] remove escort_active
tag @e[type=armor_stand,tag=guide_leovic_registre_port] remove escort_pause_d_done
tag @e[type=armor_stand,tag=guide_leovic_registre_port] remove escort_pause_h_done
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run tag @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] add escort_returning
# Depuis l’ascenseur si P est défini ; sinon repli sur l’ancien retour depuis M.
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] if entity @e[type=marker,tag=wp_leovic_registre_port_p,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_PATROL_STATE 15
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] unless entity @e[type=marker,tag=wp_leovic_registre_port_p,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_PATROL_STATE 12
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_PATROL_CD 20
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] if entity @e[type=marker,tag=wp_leovic_registre_port_p,limit=1] run tp @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] @e[type=marker,tag=wp_leovic_registre_port_p,limit=1]
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] unless entity @e[type=marker,tag=wp_leovic_registre_port_p,limit=1] if entity @e[type=marker,tag=wp_leovic_registre_port_m,limit=1] run tp @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] @e[type=marker,tag=wp_leovic_registre_port_m,limit=1]
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_RETURN_TIMER 2400
