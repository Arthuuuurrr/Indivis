execute if entity @e[type=marker,tag=wp_aurele_coeur_a,limit=1] run tp @e[tag=npc_aurele_veyrane,limit=1] @e[type=marker,tag=wp_aurele_coeur_a,limit=1]
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] if entity @e[type=marker,tag=wp_aurele_coeur_a,limit=1] run tp @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] @e[type=marker,tag=wp_aurele_coeur_a,limit=1]
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_moving
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_active
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_returning
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_d_done
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_g_done
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_n_done
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_s_done
scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur] NPC_PATROL_STATE 0
scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur] NPC_PATROL_CD 0
scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur] NPC_RETURN_TIMER 0
