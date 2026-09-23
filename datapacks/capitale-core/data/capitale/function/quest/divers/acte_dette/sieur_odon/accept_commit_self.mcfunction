
scoreboard players set @s QUEST_DIVERS_ACTE_DETTE 20
scoreboard players set @s CAP_DETTE_CHOIX 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : Très bien. Le quartier des Vieilles Mécaniques s’étend autour d’un vieux clocher ; Maître Lucain tient un atelier non loin de là.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Nouvelle quête Divers — L’Acte de dette falsifié.","color":"gold"}
title @s times 5 55 15
title @s title {"text":"Nouvelle quête","color":"gold","bold":true}
title @s subtitle {"text":"L’Acte de dette falsifié","color":"white"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Trouvez Maître Lucain Perrin au Quartier des Vieilles Mécaniques, près du clocher.","color":"yellow"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.25
