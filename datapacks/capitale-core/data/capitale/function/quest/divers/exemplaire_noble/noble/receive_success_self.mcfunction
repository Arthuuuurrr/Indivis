clear @s minecraft:written_book[minecraft:custom_model_data={strings:['quest_libraire_exemplaire_noble']}] 1
scoreboard players set @s QUEST_DIVERS_LIBRAIRE_NOBLE 30
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Dame Yselle de Verceuil]","color":"yellow"},{"text":" : Bien. Dites au libraire que l’exemplaire est arrivé intact. La Couronne approuve parfois de bons textes ; il serait dommage de les laisser dormir.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Retournez voir le libraire agréé.","color":"yellow"}
title @s times 5 50 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Retournez auprès du libraire.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.32
