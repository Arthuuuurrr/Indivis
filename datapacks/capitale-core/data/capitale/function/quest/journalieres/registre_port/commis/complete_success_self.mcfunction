function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_DAILY_REGISTRE_PORT 0
scoreboard players set @s CAP_CD_REGISTRE_PORT 72000
scoreboard players operation @s CAP_CD_REGISTRE_PORT_END = #server CAP_TIME_SEC
scoreboard players add @s CAP_CD_REGISTRE_PORT_END 72000
scoreboard players add @s REP_GARDEPORT 3
function capitale:bounds/reputation_all_self
scoreboard players add @s CAP_DAILY_DONE_TOTAL 1
execute if score @s CAP_DAILY_REGISTRE_PORT_EVER matches 0 run scoreboard players add @s QUEST_JOURNALIERES 1
scoreboard players set @s CAP_DAILY_REGISTRE_PORT_EVER 1
give @s capitale_currency:martin_dor 12
function capitale:rewards/daily_bonus/roll/port_self
function capitale:reward/skills/first_daily/registre_self
function capitale:reward/skills/daily/small_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : Parfait. Douze Martins pour vos pas, et je signalerai à la Garde du Port que vous savez éviter aux quais des querelles inutiles.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Journalière terminée] Le registre mal classé — 12 Martins d’Or, Réputation Garde du Port +3.","color":"green"}
title @s times 10 65 20
title @s title {"text":"Journalière terminée","color":"gold","bold":true}
title @s subtitle {"text":"Le registre mal classé","color":"white"}
function capitale:dialogue/sound/gain_quete_self
