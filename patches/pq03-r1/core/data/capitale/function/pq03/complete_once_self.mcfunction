# PQ03-R1 — Clôture effective C06 ; décisions mémorisées, −1 à +2 ; autres récompenses conservées
execute unless score @s hcq_pq03 matches 90 run return 0
execute unless score @s CAP_PQ3_END matches 1 run return 0
execute unless score @s CAP_PQ3_DB matches 1..4 run return 0
execute if score @s CAP_PQ3_REW matches 1.. run return 0
execute store result score @s hcm_pq3_check run indivispq03 rewards_ready
execute unless score @s hcm_pq3_check matches 1 run return 0
scoreboard players set @s CAP_PQ3_REW 1
scoreboard players set @s CAP_PQ3_IMPR 0
execute if score @s CAP_PQ3_DA matches 1 run scoreboard players add @s CAP_PQ3_IMPR 1
execute if score @s CAP_PQ3_DB matches 1 run scoreboard players add @s CAP_PQ3_IMPR 1
execute if score @s CAP_PQ3_DB matches 4 run scoreboard players remove @s CAP_PQ3_IMPR 1
scoreboard players add @s REP_GARDECOEUR 0
scoreboard players operation @s REP_GARDECOEUR += @s CAP_PQ3_IMPR
function capitale:bounds/reputation_all_self
function capitale:rewards/daily_bonus/roll/tech_self
function capitale:reward/skills/core/sous_regard_coeur_self
function hc_quetes:migration/write/pq03/v100
scoreboard players set @s QUEST_PROLOGUE 40
scoreboard players set @s CAP_QUETEACTIVE 0
scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
title @s times 10 70 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"Sous le regard du Cœur","color":"white"}
function capitale:dialogue/sound/gain_quete_self
return 1
