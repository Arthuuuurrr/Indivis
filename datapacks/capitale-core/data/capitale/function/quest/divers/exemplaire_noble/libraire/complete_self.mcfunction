function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_DIVERS_LIBRAIRE_NOBLE 100
give @s capitale_currency:martin_dor 20
function capitale:rewards/daily_bonus/roll/general_self
execute if score @s CAP_LIBRAIRE_NOBLE_PASS matches 1 run function capitale:access/revoke/quartiers_hauts_nord_self
scoreboard players set @s CAP_LIBRAIRE_NOBLE_PASS 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Libraire agréé]","color":"yellow"},{"text":" : Parfait. Un livre livré sans pliure, et un destinataire satisfait : voilà qui mérite salaire.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Quête terminée] Un exemplaire à remettre — 20 Martins d’Or.","color":"green"}
function capitale:reward/skills/unique/libraire_self
title @s times 10 65 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"Un exemplaire à remettre","color":"white"}
function capitale:dialogue/sound/gain_quete_self
