scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
scoreboard players add @s REP_GARDEPORT 5
function capitale:bounds/reputation_all_self
scoreboard players set @s QUEST_GARDEPORT 100
scoreboard players set @s QUEST_PROLOGUE 30
scoreboard players set @s CAP_QUETEACTIVE 0
title @s times 10 70 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"Le registre du Port","color":"white"}
function capitale:dialogue/sound/gain_quete_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Terminée : Le registre du Port. Réputation Garde du Port +5.","color":"gold"}
function capitale:rewards/daily_bonus/roll/port_self
function capitale:reward/skills/core/registre_port_self
scoreboard players set @s CAP_QSEQ 602
scoreboard players set @s CAP_QSEQ_TIMER 60
