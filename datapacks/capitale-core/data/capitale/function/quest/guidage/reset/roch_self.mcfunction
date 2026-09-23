scoreboard players set @s CAP_FLAG 1
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_moving
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_active
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_returning
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_pause_d_done
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_crate_h_done
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_crate_h_resolved
execute if entity @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] if entity @e[type=marker,tag=wp_roch_profondeurs_a,limit=1] run tp @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] @e[type=marker,tag=wp_roch_profondeurs_a,limit=1]
execute if entity @e[tag=npc_roch_vallet,limit=1] if entity @e[type=marker,tag=wp_roch_profondeurs_a,limit=1] run tp @e[tag=npc_roch_vallet,limit=1] @e[type=marker,tag=wp_roch_profondeurs_a,limit=1]
scoreboard players set @e[type=armor_stand,tag=guide_roch_profondeurs] NPC_PATROL_STATE 0
scoreboard players set @e[type=armor_stand,tag=guide_roch_profondeurs] NPC_PATROL_CD 0
scoreboard players set @s QUEST_PROFONDEURS 1
scoreboard players set @s CAP_QUETEACTIVE 5
scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Guidage]","color":"gold","bold":true},{"text":" L’escorte de Roch a été réinitialisée. Retournez lui parler pour reprendre la descente.","color":"white"}]
function capitale:quest/objective/parler_roch_second_ascenseur_self
