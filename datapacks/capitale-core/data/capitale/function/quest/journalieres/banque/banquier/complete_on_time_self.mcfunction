clear @s minecraft:paper[minecraft:custom_model_data={strings:['documents_banque_scelles']}] 1
scoreboard players set @s QUEST_DAILY_BANQUE 0
scoreboard players set @s CAP_BANQUE_TIMER 0
scoreboard players set @s CAP_BANQUE_SCEAU 0
scoreboard players set @s CAP_CD_BANQUE 72000
scoreboard players operation @s CAP_CD_BANQUE_END = #server CAP_TIME_SEC
scoreboard players add @s CAP_CD_BANQUE_END 72000
scoreboard players add @s CAP_DAILY_DONE_TOTAL 1
execute if score @s CAP_DAILY_BANQUE_EVER matches 0 run scoreboard players add @s QUEST_JOURNALIERES 1
scoreboard players set @s CAP_DAILY_BANQUE_EVER 1
give @s capitale_currency:martin_dor 20
function capitale:rewards/daily_bonus/roll/general_self
function capitale:reward/skills/first_daily/banque_self
function capitale:reward/skills/daily/tiny_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Banquier]","color":"yellow"},{"text":" : Le sceau est intact, et le registre n’est point clos. Voilà un service mené avec diligence.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Journalière terminée] La Banque — remise à temps, 20 Martins d’Or.","color":"green"}
title @s times 10 65 20
title @s title {"text":"Journalière terminée","color":"gold","bold":true}
title @s subtitle {"text":"La Banque — à temps","color":"white"}
function capitale:dialogue/sound/gain_quete_self
