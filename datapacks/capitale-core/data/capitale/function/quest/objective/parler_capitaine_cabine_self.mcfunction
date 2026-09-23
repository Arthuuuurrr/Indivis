function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Objectif]","color":"gold","bold":true},{"text":" Parlez au capitaine Althéon dans sa cabine.","color":"gold"}]
title @s times 5 45 10
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Parlez au capitaine Althéon dans sa cabine.","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.35
