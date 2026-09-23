function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Quête] Sous pavillon de la Couronne — Retrouvez Géraud Rivet sur le pont.","color":"gold"}]
title @s times 5 50 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Retrouvez Géraud Rivet sur le pont.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
