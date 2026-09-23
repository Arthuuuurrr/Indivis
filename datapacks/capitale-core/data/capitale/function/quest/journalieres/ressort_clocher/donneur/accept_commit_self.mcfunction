
scoreboard players set @s QUEST_DAILY_RESSORT_CLOCHER 20
scoreboard players set @s CAP_RESSORT_TIMER 120
function capitale:quest/journalieres/ressort_clocher/item/give_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Commis des Messageries]","color":"yellow"},{"text":" : Prenez ce ressort. Si vous atteignez les Vieilles Mécaniques rapidement, l’Horlogère aura encore le temps de tenir l’horaire du clocher.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Journalière] Nouvelle mission — Le ressort du clocher.","color":"gold"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Rejoignez le Quartier des Vieilles Mécaniques avant que l’urgence ne retombe.","color":"yellow"}
title @s times 5 55 15
title @s title {"text":"Nouvelle journalière","color":"gold","bold":true}
title @s subtitle {"text":"Le ressort du clocher","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.25
