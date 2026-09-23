scoreboard players set @s QUEST_SIDE_BA_Q01 20
scoreboard players set @s CAP_QUETEACTIVE 9101
playsound minecraft:entity.player.attack.nodamage master @s ~ ~ ~ 0.35 1.6
playsound minecraft:block.note_block.pling master @s ~ ~ ~ 0.45 1.25
title @s title {"text":"Le Quartier des Oubliés","color":"gold"}
title @s subtitle {"text":"Parler au marchand de l’étal","color":"yellow"}
tellraw @s [{"text":"[Mira]","color":"aqua"},{"text":" : Oups— pardon !","color":"white"}]
tellraw @s [{"text":"[Marchand]","color":"aqua"},{"text":" : Reviens ici, sale petite voleuse !","color":"white"}]
tellraw @s [{"text":"[Objectif]","color":"yellow"},{"text":" Parler au marchand de l’étal.","color":"white"}]
title @s actionbar {"text":"Objectif : parler au marchand de l’étal.","color":"yellow"}
function capitale:npc/mira/path/q01/start
