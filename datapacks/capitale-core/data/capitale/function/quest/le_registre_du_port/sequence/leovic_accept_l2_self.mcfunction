function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Le registre du Port — Suivez Léovic jusqu’au magistrat.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Le registre du Port","color":"gold","bold":true}
title @s subtitle {"text":"Suivez Léovic jusqu’au magistrat.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
function capitale:quest/le_registre_du_port/escort/start_self
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
