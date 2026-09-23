
function capitale:quest/dialogue/clear_self
clear @s minecraft:paper[minecraft:custom_model_data={strings:['acte_dette_falsifie']}] 1
scoreboard players set @s QUEST_DIVERS_ACTE_DETTE 100
scoreboard players set @s CAP_DETTE_CHOIX 2
execute if score @s CAP_ACTE_SUD_PASS matches 1 if score @s CAP_PASS_ZONE matches 20 run scoreboard players set @s CAP_PASS_ZONE 0
execute if score @s CAP_ACTE_SUD_PASS matches 1 if score @s CAP_PASS_TIMER matches 1.. run scoreboard players set @s CAP_PASS_TIMER 0
scoreboard players set @s CAP_ACTE_SUD_PASS 0
scoreboard players add @s REP_PROFONDEURS 5
function capitale:bounds/reputation_all_self
give @s capitale_currency:martin_dor 20
function capitale:rewards/daily_bonus/roll/general_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Vous me rendez plus qu’un papier. Vingt Martins, ce n’est pas la fortune des Hauts Quartiers, mais les Profondeurs sauront que vous n’avez pas choisi le plus facile.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Quête terminée] L’Acte de dette falsifié — 20 Martins d’Or, Réputation Profondeurs +5.","color":"green"}
function capitale:reward/skills/unique/acte_dette_self
title @s times 10 65 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"L’Acte de dette falsifié","color":"white"}
function capitale:dialogue/sound/gain_quete_self
