scoreboard players set @s QUEST_DAILY_REGISTRE_PORT 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : Retrouvez l’Agent de quai près des registres de manutention. Dites-lui que la mention de départ a été retrouvée.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Journalière] Nouvelle quête — Le registre mal classé.","color":"gold"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Parlez à l’Agent de quai.","color":"yellow"}
title @s times 5 55 15
title @s title {"text":"Nouvelle journalière","color":"gold","bold":true}
title @s subtitle {"text":"Le registre mal classé","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.25
