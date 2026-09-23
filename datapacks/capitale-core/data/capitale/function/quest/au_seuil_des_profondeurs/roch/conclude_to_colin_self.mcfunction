function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Colin Férand tient ce poste. S’il vous manque un toit ou un repère, écoutez-le : il connaît les arrivées mieux que bien des greffiers.","color":"white"}]
scoreboard players set @s QUEST_PROFONDEURS 35
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Au seuil des Profondeurs — Parlez à Colin Férand.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Parlez à Colin Férand.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
function capitale:quest/au_seuil_des_profondeurs/escort_roch/return_start
