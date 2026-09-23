scoreboard players set @s QUEST_DIVERS_LAMPE 20
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Lanternes du Globe]","color":"yellow"},{"text":" : La huitième lanterne répond au socle. Le Globe retrouve une clarté régulière, comme si le passage respirait plus droit.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Retournez voir la Veilleuse des Profondeurs.","color":"yellow"}
title @s times 5 55 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Retournez voir la Veilleuse.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.25 1.35
