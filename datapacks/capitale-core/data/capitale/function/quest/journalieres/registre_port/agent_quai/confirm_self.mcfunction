scoreboard players set @s QUEST_DAILY_REGISTRE_PORT 2
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Agent de quai]","color":"yellow"},{"text":" : Voilà qui explique l’écart. Le départ était bien consigné ; il a seulement été jeté au mauvais tas. Dites au commis que je confirme la correction.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Retournez voir le Commis du Port.","color":"yellow"}
title @s times 5 50 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Retournez au Commis du Port.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.3
