
scoreboard players set @s QUEST_DIVERS_RELAIS_COEUR 20
scoreboard players set @s CAP_RELAIS_COEUR_A 0
scoreboard players set @s CAP_RELAIS_COEUR_B 0
scoreboard players set @s CAP_RELAIS_COEUR_C 0
scoreboard players set @s CAP_RELAIS_COEUR_COUNT 0
scoreboard players set @s CAP_PASS_ZONE 50
scoreboard players set @s CAP_PASS_TIMER 12000
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Laissez-passer reconnu pour dix minutes. Les trois relais sont à l’intérieur du Cœur ; cherchez les bornes au noyau violet.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Nouvelle quête Divers — Les Relais du Cœur.","color":"gold"}
title @s times 5 55 15
title @s title {"text":"Nouvelle quête","color":"gold","bold":true}
title @s subtitle {"text":"Les Relais du Cœur","color":"white"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Inspectez les trois relais de régulation à l’intérieur du Cœur.","color":"yellow"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.25
