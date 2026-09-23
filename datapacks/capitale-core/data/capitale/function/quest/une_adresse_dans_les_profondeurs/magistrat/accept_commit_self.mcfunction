function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_RESIDENCE_PROF 20
function capitale:quest/set_active/residence_prof_self
function capitale:quest/objective/parler_aubergiste_auberge_self
scoreboard players set @s QUEST_PROLOGUE 50
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Commencez donc par l’auberge. Une chambre suffit ; les registres n’exigent pas encore un palais.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Une adresse dans les Profondeurs — Louez une chambre à l’auberge.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Une adresse dans les Profondeurs","color":"gold","bold":true}
title @s subtitle {"text":"Louez une chambre à l’auberge.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
