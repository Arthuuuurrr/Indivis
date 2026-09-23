scoreboard players set @s QUEST_SPAWN 100
scoreboard players set @s QUEST_PROLOGUE 20
# RC9ad — enchaînement Léovic / Le registre du Port
# RC9af : 10 = objectif en attente : parler à Léovic sur les quais.
execute if score @s QUEST_GARDEPORT matches 0 run scoreboard players set @s QUEST_GARDEPORT 10
scoreboard players set @s CAP_QUETEACTIVE 0
scoreboard players add @s REP_COURONNE 2
execute if score @s CAP_RANGSOCIAL matches 0..9 run scoreboard players set @s CAP_RANGSOCIAL 10
function capitale:access/sync_tags_self
title @s times 10 70 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"Sous pavillon de la Couronne","color":"white"}
function capitale:dialogue/sound/gain_quete_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Quête] Terminée : Sous pavillon de la Couronne.","color":"gold"}]
tellraw @s [{"text":"[Réputation] ","color":"gold"},{"text":"Couronne +2","color":"white"}]
function capitale:rewards/daily_bonus/roll/general_self
function capitale:reward/skills/core/spawn_dirigeable_self
function capitale:quest/objective/parler_leovic_quais_self
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
