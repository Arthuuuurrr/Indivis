tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_moving
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_active
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_pause_d_done
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_crate_h_done
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_crate_h_resolved
execute if entity @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] run tag @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] add escort_returning
execute if entity @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] NPC_PATROL_STATE 12
execute if entity @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] NPC_PATROL_CD 20
execute if entity @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] if entity @e[type=marker,tag=wp_roch_profondeurs_m,limit=1] run tp @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] @e[type=marker,tag=wp_roch_profondeurs_m,limit=1]
execute if entity @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] NPC_RETURN_TIMER 2400
