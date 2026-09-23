
scoreboard players set @s QUEST_DIVERS_RELAIS_COEUR 0
scoreboard players set @s CAP_CD_RELAIS_COEUR 72000
scoreboard players operation @s CAP_CD_RELAIS_COEUR_END = #server CAP_TIME_SEC
scoreboard players add @s CAP_CD_RELAIS_COEUR_END 72000
execute if score @s CAP_PASS_ZONE matches 50 run scoreboard players set @s CAP_PASS_ZONE 0
execute if score @s CAP_PASS_ZONE matches 0 if score @s CAP_PASS_TIMER matches 1.. run scoreboard players set @s CAP_PASS_TIMER 0
scoreboard players add @s REP_GARDECOEUR 3
function capitale:bounds/reputation_all_self
scoreboard players add @s CAP_DAILY_DONE_TOTAL 1
execute if score @s CAP_DAILY_RELAIS_COEUR_EVER matches 0 run scoreboard players add @s QUEST_JOURNALIERES 1
execute if score @s CAP_DAILY_RELAIS_COEUR_EVER matches 0 run give @s minecraft:leather_chestplate[trim={material:'minecraft:copper',pattern:'minecraft:flow'},custom_name=[{"text":"Gilet de service du Cœur","italic":false,"color":"blue"}],lore=[[{"text":"Une tenue modeste remise après un premier contrôle complet des relais.","italic":false,"color":"gray"}]]] 1
scoreboard players set @s CAP_DAILY_RELAIS_COEUR_EVER 1
give @s capitale_currency:martin_dor 25
function capitale:rewards/daily_bonus/roll/tech_self
function capitale:reward/skills/first_daily/relais_self
function capitale:reward/skills/daily/large_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Trois relais, trois réponses nettes. Voilà vingt-cinq Martins pour votre service, et un mot favorable auprès des Gardes du Cœur.","color":"white"}]
execute if score @s CAP_DAILY_RELAIS_COEUR_EVER matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DAILY_RELAIS_COEUR_EVER matches 1 run tellraw @s [{"text":"[Récompense]","color":"gold"},{"text":" : La première inspection complète peut également vous avoir valu une tenue de service du Cœur si elle n’avait jamais été remise.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Journalière terminée] Les Relais du Cœur — 25 Martins d’Or, Réputation Gardes du Cœur +3.","color":"green"}
title @s times 10 65 20
title @s title {"text":"Journalière terminée","color":"gold","bold":true}
title @s subtitle {"text":"Les Relais du Cœur","color":"white"}
function capitale:dialogue/sound/gain_quete_self
