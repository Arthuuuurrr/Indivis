scoreboard players set @s QUEST_VN_NAVIRE 20
title @s times 5 55 15
title @s title {"text":"Nouvelle quête","color":"gold","bold":true}
title @s subtitle {"text":"Le Vent Noir","color":"white"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Les Cendres du Vent Noir IV : Le Vent Noir.","color":"gold"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Abordez le navire pirate et atteignez son pont supérieur.","color":"yellow"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.0
