clear @s capitale_currency:martin_dor 20
function capitale:quest/dialogue/clear_self
scoreboard players set @s CAP_CHAMBRE_PROF 1
scoreboard players set @s QUEST_RESIDENCE_PROF 30
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aubergiste des Profondeurs]","color":"yellow"},{"text":" : Parfait. Une chambre simple vous attend. Gardez votre souffle pour les escaliers, et retournez voir le magistrat si vous voulez que l’adresse compte officiellement.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Une adresse dans les Profondeurs — Retournez voir le magistrat des Profondeurs.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Retournez voir le magistrat des Profondeurs.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
