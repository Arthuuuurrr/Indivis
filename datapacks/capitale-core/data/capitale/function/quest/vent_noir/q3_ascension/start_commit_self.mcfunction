scoreboard players set @s QUEST_VN_ASCENSION 20
title @s times 5 55 15
title @s title {"text":"Nouvelle quête","color":"gold","bold":true}
title @s subtitle {"text":"L'Ascension","color":"white"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Les Cendres du Vent Noir III : L'Ascension.","color":"gold"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Rejoignez Maelor à la plateforme d’embarquement.","color":"yellow"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.1
