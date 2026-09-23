execute if entity @e[type=marker,tag=wp_roch_profondeurs_a,limit=1] run tp @e[tag=npc_roch_vallet,limit=1] @e[type=marker,tag=wp_roch_profondeurs_a,limit=1]
execute if entity @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] if entity @e[type=marker,tag=wp_roch_profondeurs_a,limit=1] run tp @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] @e[type=marker,tag=wp_roch_profondeurs_a,limit=1]
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_moving
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_active
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_returning
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_pause_d_done
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_crate_h_done
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_crate_h_resolved
scoreboard players set @e[type=armor_stand,tag=guide_roch_profondeurs] NPC_PATROL_STATE 0
scoreboard players set @e[type=armor_stand,tag=guide_roch_profondeurs] NPC_PATROL_CD 0
scoreboard players set @e[type=armor_stand,tag=guide_roch_profondeurs] NPC_RETURN_TIMER 0
kill @e[type=armor_stand,tag=guide_roch_profondeurs]
