scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Nous y sommes. Avant que je reprenne mon poste, reparlez-moi : il vous reste un détail utile à entendre.","color":"white"}]
scoreboard players set @s QUEST_PROFONDEURS 60
scoreboard players set @s CAP_QUETEACTIVE 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Au seuil des Profondeurs — Reparlez à Colin Férand devant l’auberge.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Reparlez à Colin Férand.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
