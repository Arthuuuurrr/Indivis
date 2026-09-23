function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_SIDE_BA_Q01 20
scoreboard players set @s CAP_QUETEACTIVE 9101
playsound minecraft:block.note_block.pling master @s ~ ~ ~ 0.35 1.1
title @s title {"text":"Le Quartier des Oubliés","color":"gold"}
title @s subtitle {"text":"Parler au marchand de l’étal","color":"yellow"}
tellraw @s [{"text":"[Scène]","color":"gold"},{"text":" Une jeune fille file entre les étals pendant que le marchand crie derrière elle.","color":"white"}]
tellraw @s [{"text":"[Objectif]","color":"yellow"},{"text":" Parler au marchand de l’étal.","color":"white"}]
title @s actionbar {"text":"Objectif : parler au marchand de l’étal.","color":"yellow"}
