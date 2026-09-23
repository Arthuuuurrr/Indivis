scoreboard players set @s QUEST_DIVERS_LAMPE 0
scoreboard players set @s CAP_CD_GLOBE 72000
scoreboard players operation @s CAP_CD_GLOBE_END = #server CAP_TIME_SEC
scoreboard players add @s CAP_CD_GLOBE_END 72000
scoreboard players set @s CAP_DISC_PROFONDEURS 1
scoreboard players add @s CAP_DAILY_DONE_TOTAL 1
execute if score @s CAP_DAILY_GLOBE_EVER matches 0 run scoreboard players add @s QUEST_JOURNALIERES 1
scoreboard players set @s CAP_DAILY_GLOBE_EVER 1
scoreboard players set @s CAP_GLOBE_DONE 0
scoreboard players set @s CAP_GLOBE_L1 0
scoreboard players set @s CAP_GLOBE_L2 0
scoreboard players set @s CAP_GLOBE_L3 0
scoreboard players set @s CAP_GLOBE_L4 0
scoreboard players set @s CAP_GLOBE_L5 0
scoreboard players set @s CAP_GLOBE_L6 0
scoreboard players set @s CAP_GLOBE_L7 0
scoreboard players set @s CAP_GLOBE_L8 0
give @s capitale_currency:martin_dor 20
function capitale:rewards/daily_bonus/roll/profondeurs_self
function capitale:reward/skills/first_daily/globe_self
function capitale:reward/skills/daily/small_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Veilleuse des Profondeurs]","color":"yellow"},{"text":" : Voilà. Le Globe respire mieux. Peu de gens regardent ces lumières, mais beaucoup dépendent d’elles sans le savoir.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Journalière terminée] Les Lanternes du Globe — 20 Martins d’Or.","color":"green"}
title @s times 10 65 20
title @s title {"text":"Journalière terminée","color":"gold","bold":true}
title @s subtitle {"text":"Les Lanternes du Globe","color":"white"}
function capitale:dialogue/sound/gain_quete_self
