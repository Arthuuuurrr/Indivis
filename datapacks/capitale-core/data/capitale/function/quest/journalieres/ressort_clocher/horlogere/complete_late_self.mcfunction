
clear @s minecraft:tripwire_hook[minecraft:custom_model_data={strings:['ressort_clocher']}] 1
scoreboard players set @s QUEST_DAILY_RESSORT_CLOCHER 0
scoreboard players set @s CAP_RESSORT_TIMER 0
scoreboard players set @s CAP_CD_RESSORT_CLOCHER 72000
scoreboard players operation @s CAP_CD_RESSORT_CLOCHER_END = #server CAP_TIME_SEC
scoreboard players add @s CAP_CD_RESSORT_CLOCHER_END 72000
scoreboard players add @s CAP_DAILY_DONE_TOTAL 1
execute if score @s CAP_DAILY_RESSORT_CLOCHER_EVER matches 0 run scoreboard players add @s QUEST_JOURNALIERES 1
scoreboard players set @s CAP_DAILY_RESSORT_CLOCHER_EVER 1
give @s capitale_currency:martin_dor 10
function capitale:rewards/daily_bonus/roll/tech_reduced_self
function capitale:reward/skills/first_daily/ressort_self
function capitale:reward/skills/daily/tiny_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Horlogère du Clocher]","color":"yellow"},{"text":" : Vous y êtes arrivé, c’est déjà cela. Dix Martins : la pièce servira, mais l’urgence a perdu son prix.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Journalière terminée] Le ressort du clocher — livraison tardive, 10 Martins d’Or.","color":"green"}
title @s times 10 65 20
title @s title {"text":"Journalière terminée","color":"gold","bold":true}
title @s subtitle {"text":"Le ressort du clocher — tardif","color":"white"}
function capitale:dialogue/sound/gain_quete_self
