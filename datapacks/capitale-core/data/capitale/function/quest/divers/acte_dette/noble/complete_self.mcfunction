
clear @s minecraft:paper[minecraft:custom_model_data={strings:['acte_dette_falsifie']}] 1
scoreboard players set @s QUEST_DIVERS_ACTE_DETTE 100
scoreboard players set @s CAP_DETTE_CHOIX 1
execute if score @s CAP_ACTE_SUD_PASS matches 1 if score @s CAP_PASS_ZONE matches 20 run scoreboard players set @s CAP_PASS_ZONE 0
execute if score @s CAP_ACTE_SUD_PASS matches 1 if score @s CAP_PASS_TIMER matches 1.. run scoreboard players set @s CAP_PASS_TIMER 0
scoreboard players set @s CAP_ACTE_SUD_PASS 0
scoreboard players add @s REP_NOBLESSE 3
function capitale:bounds/reputation_all_self
give @s capitale_currency:martin_dor 30
function capitale:rewards/daily_bonus/roll/general_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Dame Éléonore de Vaudrec]","color":"yellow"},{"text":" : Lucain Perrin a signé. Qu’il regrette les termes après avoir joui du délai ne change pas l’ordre des choses. Une ville tient aussi parce que les papiers survivent aux émotions.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Dame Éléonore de Vaudrec]","color":"yellow"},{"text":" : Odon a ses méthodes, je le sais. Mais sans hommes comme lui, les familles comme la mienne devraient exposer chaque créance aux cris de la rue. Je vous paie trente Martins pour avoir évité cela.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Quête terminée] L’Acte de dette falsifié — 30 Martins d’Or, Réputation Noblesse +3.","color":"green"}
function capitale:reward/skills/unique/acte_dette_self
title @s times 10 65 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"L’Acte de dette falsifié","color":"white"}
function capitale:dialogue/sound/gain_quete_self
