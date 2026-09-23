scoreboard players set @s CAP_FLAG 1
tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_moving
tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_active
tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_returning
tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_pause_d_done
tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_pause_h_done
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] if entity @e[type=marker,tag=wp_colin_profondeurs_a,limit=1] run tp @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] @e[type=marker,tag=wp_colin_profondeurs_a,limit=1]
execute if entity @e[tag=npc_colin_ferand,limit=1] if entity @e[type=marker,tag=wp_colin_profondeurs_a,limit=1] run tp @e[tag=npc_colin_ferand,limit=1] @e[type=marker,tag=wp_colin_profondeurs_a,limit=1]
scoreboard players set @e[type=armor_stand,tag=guide_colin_profondeurs] NPC_PATROL_STATE 0
scoreboard players set @e[type=armor_stand,tag=guide_colin_profondeurs] NPC_PATROL_CD 0
scoreboard players set @s QUEST_PROFONDEURS 40
scoreboard players set @s CAP_QUETEACTIVE 5
scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Guidage]","color":"gold","bold":true},{"text":" L’escorte de Colin a été réinitialisée. Retournez lui parler pour reprendre la route vers l’auberge.","color":"white"}]
function capitale:quest/objective/parler_aubergiste_auberge_self
