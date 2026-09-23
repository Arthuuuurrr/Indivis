scoreboard players set @s CAP_FLAG 1
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_moving
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_active
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_returning
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_d_done
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_g_done
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_n_done
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_s_done
execute if score @s QUEST_GARDECOEUR matches 20 if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] if entity @e[type=marker,tag=wp_aurele_coeur_a,limit=1] run tp @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] @e[type=marker,tag=wp_aurele_coeur_a,limit=1]
execute if score @s QUEST_GARDECOEUR matches 40 if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] if entity @e[type=marker,tag=wp_aurele_coeur_a,limit=1] run tp @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] @e[type=marker,tag=wp_aurele_coeur_a,limit=1]
scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur] NPC_PATROL_CD 0
# Retour Aurèle au point d’attente
execute if entity @e[type=marker,tag=wp_aurele_coeur_a,limit=1] run tp @e[tag=npc_aurele_veyrane,limit=1] @e[type=marker,tag=wp_aurele_coeur_a,limit=1]
execute if score @s QUEST_GARDECOEUR matches 20 run scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur] NPC_PATROL_STATE 0
execute if score @s QUEST_GARDECOEUR matches 40 run scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur] NPC_PATROL_STATE 0
execute if score @s QUEST_GARDECOEUR matches 20 run scoreboard players set @s QUEST_GARDECOEUR 1
execute if score @s QUEST_GARDECOEUR matches 40 run scoreboard players set @s QUEST_GARDECOEUR 30
scoreboard players set @s CAP_QUETEACTIVE 3
scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Guidage]","color":"gold","bold":true},{"text":" L’escorte d’Aurèle a été réinitialisée. Reprenez le dialogue auprès d’elle.","color":"white"}]
execute if score @s QUEST_GARDECOEUR matches 1 run function capitale:quest/objective/parler_aurele_sortie_ascenseur_coeur_self
execute if score @s QUEST_GARDECOEUR matches 30 run function capitale:quest/objective/parler_aurele_sortie_ascenseur_coeur_self
