tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_moving
tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_active
tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_pause_d_done
tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_pause_h_done
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run tag @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] add escort_returning
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] NPC_PATROL_STATE 25
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] NPC_PATROL_CD 20
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] if entity @e[type=marker,tag=wp_colin_profondeurs_z,limit=1] run tp @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] @e[type=marker,tag=wp_colin_profondeurs_z,limit=1]
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] NPC_RETURN_TIMER 2400
