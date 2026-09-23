function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_DIVERS_LAMPE 10
scoreboard players set @s CAP_DISC_PROFONDEURS 1
scoreboard players set @s CAP_GLOBE_DONE 0
scoreboard players set @s CAP_GLOBE_L1 0
scoreboard players set @s CAP_GLOBE_L2 0
scoreboard players set @s CAP_GLOBE_L3 0
scoreboard players set @s CAP_GLOBE_L4 0
scoreboard players set @s CAP_GLOBE_L5 0
scoreboard players set @s CAP_GLOBE_L6 0
scoreboard players set @s CAP_GLOBE_L7 0
scoreboard players set @s CAP_GLOBE_L8 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Veilleuse des Profondeurs]","color":"yellow"},{"text":" : Recalibrez les huit lanternes du socle du Globe. Touchez chaque ferrure avec soin ; si l’une reste fausse, l’ensemble tirera de travers.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Recalibrez les 8 lanternes du socle du Globe terrestre des Profondeurs.","color":"yellow"}
title @s times 5 55 15
title @s title {"text":"Nouvelle quête","color":"gold","bold":true}
title @s subtitle {"text":"Les Lanternes du Globe","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.25 1.25
