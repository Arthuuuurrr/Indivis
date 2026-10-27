# PQ02-R1 — Achèvement explicite et unique ; compétences/XP/bonus conservés ; +5 supprimé
execute unless score @s hcq_pq02 matches 90 run return 0
execute unless score @s CAP_QDIALOG_OWNER matches 97 run return 0
execute unless score @s CAP_PQ2_REG matches 1 run return 0
execute unless score @s CAP_PQ2_DD matches 1..4 run return 0
execute if score @s CAP_PQ2_REW matches 1.. run return 0
execute store result score @s hcm_pq2_check run indivispq02 rewards_ready
execute unless score @s hcm_pq2_check matches 1 run return 0
scoreboard players set @s CAP_PQ2_REW 1
function capitale:reward/skills/core/registre_port_self
function capitale:rewards/daily_bonus/roll/port_self
function hc_quetes:migration/write/pq02/v100
scoreboard players set @s QUEST_PROLOGUE 30
scoreboard players set @s CAP_QUETEACTIVE 0
scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
title @s times 10 70 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"Le registre du Port","color":"white"}
function capitale:dialogue/sound/gain_quete_self
return 1
