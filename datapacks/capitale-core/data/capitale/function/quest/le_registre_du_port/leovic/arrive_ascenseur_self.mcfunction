scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0

function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Voilà l’ascenseur. Avant que je reprenne ma ronde, venez me reparler : votre arrivée mérite d’être close proprement.","color":"white"}]
scoreboard players set @s QUEST_GARDEPORT 60
scoreboard players set @s CAP_QUETEACTIVE 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Le registre du Port — Reparlez à Léovic devant l’ascenseur.","color":"gold"}
title @s times 5 50 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Reparlez à Léovic.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
