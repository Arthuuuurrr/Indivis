scoreboard players set @s QUEST_VN_ASCENSION 100
scoreboard players set @s CAP_QUETEACTIVE 0
give @s capitale_currency:martin_dor 45
title @s times 10 65 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"L'Ascension","color":"white"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Terminée : L'Ascension.","color":"gold"}
function capitale:rewards/daily_bonus/roll/profondeurs_self
function capitale:reward/skills/core/vent_noir_q3_self
function capitale:dialogue/sound/gain_quete_self
function capitale:quest/vent_noir/q4_le_vent_noir/maelor/offer_after_ascent_self
