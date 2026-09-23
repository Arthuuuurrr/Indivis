function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_PROFONDEURS 20
scoreboard players set @s QUEST_PROLOGUE 40
scoreboard players set @s CAP_QUETEACTIVE 5
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Très bien. Marchez à côté de moi, pas derrière les piliers : ici, on se perd plus vite qu’on ne l’admet.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Au seuil des Profondeurs — Suivez Roch Vallet jusqu’au poste inférieur.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Au seuil des Profondeurs","color":"gold","bold":true}
title @s subtitle {"text":"Suivez Roch Vallet jusqu’au poste inférieur.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
function capitale:quest/au_seuil_des_profondeurs/escort_roch/start_self
