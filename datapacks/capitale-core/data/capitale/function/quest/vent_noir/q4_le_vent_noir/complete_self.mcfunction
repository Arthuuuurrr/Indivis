scoreboard players set @s QUEST_VN_NAVIRE 100
scoreboard players set @s CAP_QUETEACTIVE 0
scoreboard players add @s REP_PROFONDEURS 5
function capitale:bounds/reputation_all_self
give @s capitale_currency:martin_dor 75
title @s times 10 80 25
title @s title {"text":"Ligne terminée","color":"gold","bold":true}
title @s subtitle {"text":"Les Cendres du Vent Noir","color":"white"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Terminée : Le Vent Noir.","color":"gold"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Ligne terminée : Les Cendres du Vent Noir. 75 Martins d’Or, Réputation Profondeurs +5.","color":"gold"}
function capitale:rewards/daily_bonus/roll/profondeurs_self
function capitale:reward/skills/core/vent_noir_q4_self
function capitale:dialogue/sound/gain_quete_self
