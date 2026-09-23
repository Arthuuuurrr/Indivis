scoreboard players set @s QUEST_VN_DOCKS 20
title @s times 5 55 15
title @s title {"text":"Nouvelle quête","color":"gold","bold":true}
title @s subtitle {"text":"Les Ombres des Docks","color":"white"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Les Cendres du Vent Noir II : Les Ombres des Docks.","color":"gold"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Commencez par interroger le Guetteur des quais.","color":"yellow"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.15
