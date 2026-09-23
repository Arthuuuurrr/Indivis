
clear @s capitale_currency:martin_dor 100
function capitale:quest/dialogue/clear_self
execute if score @s CAP_RANGSOCIAL matches ..19 run scoreboard players set @s CAP_RANGSOCIAL 20
function capitale:bounds/rangsocial_self
function capitale:access/recalculate_by_rank_self
function capitale:display/prefix/sync_self
scoreboard players add @s REP_PROFONDEURS 3
function capitale:bounds/reputation_all_self
scoreboard players set @s QUEST_RESIDENCE_PROF 100
scoreboard players set @s QUEST_PROLOGUE 100
scoreboard players set @s CAP_QUETEACTIVE 0
execute if score @s CAP_RANGSOCIAL matches ..20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_RANGSOCIAL matches ..20 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Les frais sont réglés. Votre chambre est portée au registre : vous êtes désormais reconnu comme résident inscrit de la Capitale.","color":"white"}]
execute if score @s CAP_RANGSOCIAL matches 21.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_RANGSOCIAL matches 21.. run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Les frais sont réglés. Votre chambre est portée au registre ; votre qualité excède déjà celle d’un simple résident, nul reclassement n’est requis.","color":"white"}]
title @s times 10 70 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"Une adresse dans les Profondeurs","color":"white"}
function capitale:dialogue/sound/gain_quete_self
execute if score @s CAP_RANGSOCIAL matches ..20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_RANGSOCIAL matches ..20 run tellraw @s {"text":"[Quête] Terminée : Une adresse dans les Profondeurs. Rang social : Résident inscrit. Réputation Profondeurs +3.","color":"gold"}
execute if score @s CAP_RANGSOCIAL matches 21.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_RANGSOCIAL matches 21.. run tellraw @s {"text":"[Quête] Terminée : Une adresse dans les Profondeurs. Logis inscrit ; rang social conservé. Réputation Profondeurs +3.","color":"gold"}
function capitale:quest/principal/citoyennete/start_self
