
clear @s minecraft:tripwire_hook[minecraft:custom_model_data={strings:['ressort_clocher']}] 1
scoreboard players set @s QUEST_DAILY_RESSORT_CLOCHER 0
scoreboard players set @s CAP_RESSORT_TIMER 0
scoreboard players set @s CAP_CD_RESSORT_CLOCHER 72000
scoreboard players operation @s CAP_CD_RESSORT_CLOCHER_END = #server CAP_TIME_SEC
scoreboard players add @s CAP_CD_RESSORT_CLOCHER_END 72000
scoreboard players add @s CAP_DAILY_DONE_TOTAL 1
execute if score @s CAP_DAILY_RESSORT_CLOCHER_EVER matches 0 run scoreboard players add @s QUEST_JOURNALIERES 1
execute if score @s CAP_DAILY_RESSORT_CLOCHER_EVER matches 0 run give @s minecraft:leather_boots[trim={material:'minecraft:copper',pattern:'minecraft:flow'},custom_name=[{"text":"Bottes d’atelier","italic":false,"color":"blue"}],lore=[[{"text":"Une paire remise pour un service prompt dans les Vieilles Mécaniques.","italic":false,"color":"gray"}]]] 1
scoreboard players set @s CAP_DAILY_RESSORT_CLOCHER_EVER 1
give @s capitale_currency:martin_dor 20
function capitale:rewards/daily_bonus/roll/tech_self
function capitale:reward/skills/first_daily/ressort_self
function capitale:reward/skills/daily/normal_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Horlogère du Clocher]","color":"yellow"},{"text":" : À temps. Voilà vingt Martins : le clocher gardera son heure, et les Vieilles Mécaniques leur réputation.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Journalière terminée] Le ressort du clocher — livraison à temps, 20 Martins d’Or.","color":"green"}
title @s times 10 65 20
title @s title {"text":"Journalière terminée","color":"gold","bold":true}
title @s subtitle {"text":"Le ressort du clocher — à temps","color":"white"}
function capitale:dialogue/sound/gain_quete_self
