
function capitale:quest/dialogue/clear_self
clear @s capitale_currency:martin_dor 500
scoreboard players set @s QUEST_CITOYENNETE 100
execute if score @s CAP_RANGSOCIAL matches ..29 run scoreboard players set @s CAP_RANGSOCIAL 30
function capitale:bounds/rangsocial_self
function capitale:access/recalculate_by_rank_self
function capitale:display/prefix/sync_self
execute if score @s CAP_RANGSOCIAL matches ..30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_RANGSOCIAL matches ..30 run tellraw @s [{"text":"[Magistrat]","color":"yellow"},{"text":" : Les droits sont réglés. Votre nom ne figure plus seulement parmi les résidents : vous êtes désormais Citoyen de la Capitale.","color":"white"}]
execute if score @s CAP_RANGSOCIAL matches 31.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_RANGSOCIAL matches 31.. run tellraw @s [{"text":"[Magistrat]","color":"yellow"},{"text":" : Les droits sont réglés. La procédure de citoyenneté est close ; votre qualité excédait déjà cette marche, le registre n’altère donc point votre rang.","color":"white"}]
execute if score @s CAP_RANGSOCIAL matches ..30 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_RANGSOCIAL matches ..30 run tellraw @s {"text":"[Objectif principal terminé] Citoyenneté de la Capitale obtenue. Rang social : Citoyen de la Capitale.","color":"green"}
execute if score @s CAP_RANGSOCIAL matches 31.. run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_RANGSOCIAL matches 31.. run tellraw @s {"text":"[Objectif principal terminé] Citoyenneté de la Capitale validée. Rang social conservé.","color":"green"}
title @s times 10 80 25
execute if score @s CAP_RANGSOCIAL matches ..30 run title @s title {"text":"Rang acquis","color":"gold","bold":true}
execute if score @s CAP_RANGSOCIAL matches ..30 run title @s subtitle {"text":"Citoyen de la Capitale","color":"green"}
execute if score @s CAP_RANGSOCIAL matches 31.. run title @s title {"text":"Droits validés","color":"gold","bold":true}
execute if score @s CAP_RANGSOCIAL matches 31.. run title @s subtitle {"text":"Citoyenneté reconnue","color":"green"}
function capitale:dialogue/sound/gain_quete_self
