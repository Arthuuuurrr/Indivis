particle minecraft:damage_indicator ~ ~1.0 ~ 0.34 0.44 0.34 0.13 20 force @a[distance=..24]
playsound minecraft:entity.player.attack.crit player @a[distance=..24] ~ ~ ~ 1.00 0.78 0
damage @s 5 minecraft:generic by @a[tag=capskills_0128.double_lame.caster,limit=1]
effect give @s minecraft:weakness 3 0 true
effect give @s minecraft:glowing 3 0 true
title @a[tag=capskills_0128.double_lame.caster,limit=1] actionbar {"text":"Sentence exécutée — paire assortie.","color":"red"}
