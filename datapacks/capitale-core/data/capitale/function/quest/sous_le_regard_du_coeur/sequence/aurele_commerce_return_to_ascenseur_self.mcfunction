# RC9ah — retour progressif d’Aurèle depuis le Cercle marchand vers le point A.
# Ancien comportement : TP direct Aurèle + guide au point A après la présentation.
# Nouveau comportement : C -> B -> A via la route inverse, puis mise à jour de l’objectif au retour effectif.

function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_GARDECOEUR 25
scoreboard players set @s CAP_QUETEACTIVE 3
scoreboard players set @s CAP_GUIDE_LOCK 1
scoreboard players set @s CAP_GUIDE_ID 2
scoreboard players set @s CAP_GUIDE_MISS_T 0

# Prépare le guide au retour depuis le point d’arrivée du Cercle marchand.
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_moving
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_active
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_d_done
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_g_done
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_n_done
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_pause_s_done
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run tag @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] add escort_returning
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_PATROL_STATE 8
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_PATROL_CD 20
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] run scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_RETURN_TIMER 2400

function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Prenez le temps de vous repérer. Je retourne à la sortie du premier ascenseur ; retrouvez-moi là-bas lorsque vous voudrez descendre vers les Profondeurs.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Sous le regard du Cœur — Aurèle retourne à l’ascenseur. Retrouvez-la là-bas quand vous serez prêt.","color":"gold"}
title @s times 5 55 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Retrouvez Aurèle à l’ascenseur.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.25 1.35

scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
