scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
scoreboard players add @s REP_GARDECOEUR 5
function capitale:bounds/reputation_all_self
scoreboard players set @s QUEST_GARDECOEUR 100
scoreboard players set @s QUEST_PROLOGUE 40
scoreboard players set @s CAP_QUETEACTIVE 0
title @s times 10 70 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"Sous le regard du Cœur","color":"white"}
function capitale:dialogue/sound/gain_quete_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Terminée : Sous le regard du Cœur. Réputation Gardes du Cœur +5.","color":"gold"}
function capitale:rewards/daily_bonus/roll/tech_self
function capitale:reward/skills/core/sous_regard_coeur_self
scoreboard players set @s CAP_QSEQ 722
scoreboard players set @s CAP_QSEQ_TIMER 60
