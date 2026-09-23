scoreboard players set @s QUEST_VN_DOCKS 100
scoreboard players set @s CAP_QUETEACTIVE 0
give @s capitale_currency:martin_dor 35
title @s times 10 65 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"Les Ombres des Docks","color":"white"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Terminée : Les Ombres des Docks. 35 Martins d’Or.","color":"gold"}
function capitale:rewards/daily_bonus/roll/port_self
function capitale:reward/skills/core/vent_noir_q2_self
function capitale:dialogue/sound/gain_quete_self
function capitale:quest/vent_noir/q3_ascension/maelor/offer_after_q2_self
