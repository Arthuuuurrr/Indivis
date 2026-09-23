function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_PROFONDEURS 50
scoreboard players set @s CAP_QUETEACTIVE 5
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Très bien. L’auberge reçoit les nouveaux venus avant que les registres ne les rattrapent. Suivez-moi.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Au seuil des Profondeurs — Suivez Colin Férand jusqu’à l’auberge.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Suivez Colin Férand jusqu’à l’auberge.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
function capitale:quest/au_seuil_des_profondeurs/escort_colin/start_self
