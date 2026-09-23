
function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_DAILY_TEMOIGNAGES_QUAIS 0
scoreboard players set @s CAP_CD_TEMOIGNAGES_QUAIS 72000
scoreboard players operation @s CAP_CD_TEMOIGNAGES_QUAIS_END = #server CAP_TIME_SEC
scoreboard players add @s CAP_CD_TEMOIGNAGES_QUAIS_END 72000
scoreboard players add @s REP_GARDEPORT 1
function capitale:bounds/reputation_all_self
scoreboard players add @s CAP_DAILY_DONE_TOTAL 1
execute if score @s CAP_DAILY_TEMOIGNAGES_QUAIS_EVER matches 0 run scoreboard players add @s QUEST_JOURNALIERES 1
scoreboard players set @s CAP_DAILY_TEMOIGNAGES_QUAIS_EVER 1
give @s capitale_currency:martin_dor 15
function capitale:rewards/daily_bonus/roll/port_reduced_self
function capitale:reward/skills/first_daily/temoins_self
function capitale:reward/skills/daily/small_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : C’est recevable. Pas complet, mais assez solide pour éviter que deux signatures se disputent toute la nuit. Quinze Martins.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Journalière terminée] Trois témoignages sur les quais — rapport partiel, 15 Martins d’Or, Réputation Port +1.","color":"green"}
title @s times 10 65 20
title @s title {"text":"Journalière terminée","color":"gold","bold":true}
title @s subtitle {"text":"Trois témoignages — rapport partiel","color":"white"}
function capitale:dialogue/sound/gain_quete_self
