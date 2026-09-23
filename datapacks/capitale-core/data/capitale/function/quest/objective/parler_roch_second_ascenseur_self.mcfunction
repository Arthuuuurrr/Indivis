function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Objectif]","color":"gold","bold":true},{"text":" Parlez à Roch à la sortie du second ascenseur, là où les Profondeurs rejoignent les niveaux supérieurs.","color":"gold"}]
title @s times 5 45 10
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Parlez à Roch à la sortie du second ascenseur, là où les Profondeurs rejoignent les niveaux supérieurs.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
