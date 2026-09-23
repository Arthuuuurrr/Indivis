function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Objectif]","color":"gold","bold":true},{"text":" Parlez à Aurèle à la sortie du premier ascenseur, au milieu du Cœur.","color":"gold"}]
title @s times 5 45 10
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Parlez à Aurèle à la sortie du premier ascenseur, au milieu du Cœur.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
