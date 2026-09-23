scoreboard players set @s QUEST_VN_CAPITAINE 100
scoreboard players set @s CAP_QUETEACTIVE 0
give @s capitale_currency:martin_dor 35
title @s times 10 65 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"Un capitaine sans navire","color":"white"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Terminée : Un capitaine sans navire. 35 Martins d’Or.","color":"gold"}
function capitale:rewards/daily_bonus/roll/port_self
function capitale:reward/skills/core/vent_noir_q1_self
function capitale:dialogue/sound/gain_quete_self
function capitale:quest/vent_noir/q2_ombres_des_docks/maelor/offer_after_q1_self
