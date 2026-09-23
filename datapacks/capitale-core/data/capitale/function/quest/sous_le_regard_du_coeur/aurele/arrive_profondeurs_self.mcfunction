scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0

function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Nous sommes au seuil des Profondeurs. Avant que vous descendiez, reparlez-moi : ce passage mérite plus qu’un signe de tête.","color":"white"}]
scoreboard players set @s QUEST_GARDECOEUR 50
scoreboard players set @s CAP_QUETEACTIVE 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Sous le regard du Cœur — Reparlez à Aurèle devant l’ascenseur des Profondeurs.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Reparlez à Aurèle.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
