
scoreboard players set @s QUEST_DAILY_TEMOIGNAGES_QUAIS 0
scoreboard players set @s CAP_CD_TEMOIGNAGES_QUAIS 72000
scoreboard players operation @s CAP_CD_TEMOIGNAGES_QUAIS_END = #server CAP_TIME_SEC
scoreboard players add @s CAP_CD_TEMOIGNAGES_QUAIS_END 72000
scoreboard players add @s REP_GARDEPORT 3
function capitale:bounds/reputation_all_self
scoreboard players add @s CAP_DAILY_DONE_TOTAL 1
execute if score @s CAP_DAILY_TEMOIGNAGES_QUAIS_EVER matches 0 run scoreboard players add @s QUEST_JOURNALIERES 1
execute if score @s CAP_DAILY_TEMOIGNAGES_QUAIS_EVER matches 0 run give @s minecraft:leather_helmet[trim={material:'minecraft:copper',pattern:'minecraft:flow'},custom_name=[{"text":"Capuche du Port","italic":false,"color":"blue"}],lore=[[{"text":"Un couvre-chef remis pour un rapport mené jusqu’au dernier témoignage.","italic":false,"color":"gray"}]]] 1
scoreboard players set @s CAP_DAILY_TEMOIGNAGES_QUAIS_EVER 1
give @s capitale_currency:martin_dor 25
function capitale:rewards/daily_bonus/roll/port_self
function capitale:reward/skills/first_daily/temoins_self
function capitale:reward/skills/daily/large_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Voilà un rapport complet. Trois récits, une contradiction visible, et un litige qui sera classé avec autre chose que des haussements d’épaules. Vingt-cinq Martins.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Journalière terminée] Trois témoignages sur les quais — rapport complet, 25 Martins d’Or, Réputation Port +3.","color":"green"}
title @s times 10 65 20
title @s title {"text":"Journalière terminée","color":"gold","bold":true}
title @s subtitle {"text":"Trois témoignages — rapport complet","color":"white"}
function capitale:dialogue/sound/gain_quete_self
