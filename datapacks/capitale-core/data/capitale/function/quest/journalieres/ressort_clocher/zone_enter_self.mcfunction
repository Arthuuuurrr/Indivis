
scoreboard players set @s QUEST_DAILY_RESSORT_CLOCHER 30
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Lieu identifié] Quartier des Vieilles Mécaniques.","color":"aqua"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Trouvez l’Horlogère du Clocher et remettez-lui le ressort calibré.","color":"yellow"}
title @s times 5 55 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Trouvez l’Horlogère du Clocher.","color":"white"}
execute at @s run playsound minecraft:block.bell.resonate master @s ~ ~ ~ 5.00 1.25
