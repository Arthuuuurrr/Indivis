clear @s minecraft:paper[minecraft:custom_model_data={strings:['documents_banque_scelles']}] 1
clear @s minecraft:paper[minecraft:custom_model_data={strings:['documents_banque_lus']}] 1
scoreboard players set @s QUEST_DAILY_BANQUE 0
scoreboard players set @s CAP_BANQUE_TIMER 0
scoreboard players set @s CAP_BANQUE_SCEAU 0
scoreboard players set @s CAP_CD_BANQUE 72000
scoreboard players operation @s CAP_CD_BANQUE_END = #server CAP_TIME_SEC
scoreboard players add @s CAP_CD_BANQUE_END 72000
function capitale:dialogue/sound/reponse_attendue_self
tellraw @s [{"text":"[Banquier]","color":"yellow"},{"text":" : Le sceau est rompu. La Banque refuse les documents ouverts et ne paie point les curiosités indiscrètes.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Journalière échouée] La Banque — sceau rompu.","color":"red"}
title @s times 10 65 20
title @s title {"text":"Journalière échouée","color":"red","bold":true}
title @s subtitle {"text":"Le sceau a été rompu","color":"white"}
execute at @s run playsound minecraft:block.anvil.land master @s ~ ~ ~ 0.9 1.25
