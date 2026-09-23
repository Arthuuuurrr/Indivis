function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_VN_CAPITAINE 20
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Alors restez près de moi. Les Écorcheurs du Ciel préfèrent frapper là où personne ne regarde.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Nouvelle quête — Les Cendres du Vent Noir I : Un capitaine sans navire.","color":"gold"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Restez auprès de Maelor et repoussez les hommes de Rask.","color":"yellow"}
title @s times 5 55 15
title @s title {"text":"Nouvelle quête","color":"gold","bold":true}
title @s subtitle {"text":"Un capitaine sans navire","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.25
