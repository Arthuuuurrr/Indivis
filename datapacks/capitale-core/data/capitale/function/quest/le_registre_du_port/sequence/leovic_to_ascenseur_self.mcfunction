
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Suivez-moi encore un instant. Je vais vous montrer l’ascenseur qui mène vers le Cercle commercial.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Le registre du Port — Suivez Léovic jusqu’à l’ascenseur du Cercle commercial.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Suivez Léovic jusqu’à l’ascenseur du Cercle commercial.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
scoreboard players set @s QUEST_GARDEPORT 50
scoreboard players set @s CAP_QUETEACTIVE 4
function capitale:quest/le_registre_du_port/escort/start_to_ascenseur_self
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
