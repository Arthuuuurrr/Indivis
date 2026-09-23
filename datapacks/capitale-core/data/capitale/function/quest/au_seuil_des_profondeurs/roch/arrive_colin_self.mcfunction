scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Le poste inférieur est là. Avant de repartir, venez me reparler : je veux vous remettre proprement à celui qui prend la suite.","color":"white"}]
scoreboard players set @s QUEST_PROFONDEURS 30
scoreboard players set @s CAP_QUETEACTIVE 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Au seuil des Profondeurs — Reparlez à Roch Vallet devant le poste inférieur.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Reparlez à Roch Vallet.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
