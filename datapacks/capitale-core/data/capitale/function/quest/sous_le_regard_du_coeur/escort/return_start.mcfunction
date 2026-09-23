tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_moving
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_active
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_d_done
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_g_done
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_n_done
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_s_done
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run tag @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] add escort_returning
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_PATROL_STATE 21
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_PATROL_CD 20
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] if entity @e[type=marker,tag=wp_aurele_coeur_v,limit=1] run tp @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] @e[type=marker,tag=wp_aurele_coeur_v,limit=1]
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_RETURN_TIMER 2400
