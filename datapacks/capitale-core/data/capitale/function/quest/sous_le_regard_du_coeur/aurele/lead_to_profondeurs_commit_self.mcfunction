function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_GARDECOEUR 40
scoreboard players set @s CAP_QUETEACTIVE 3
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Très bien. Restez près de moi ; les accès se ressemblent vite lorsque l’on ne connaît pas encore le Cœur.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Sous le regard du Cœur — Suivez Aurèle jusqu’à l’ascenseur des Profondeurs.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Suivez Aurèle jusqu’à l’ascenseur des Profondeurs.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
function capitale:quest/sous_le_regard_du_coeur/escort/start_to_profondeurs_self
