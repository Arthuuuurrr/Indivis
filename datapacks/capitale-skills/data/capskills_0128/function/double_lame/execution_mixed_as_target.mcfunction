particle minecraft:damage_indicator ~ ~1.0 ~ 0.32 0.42 0.32 0.12 18 force @a[distance=..24]
playsound minecraft:entity.player.attack.crit player @a[distance=..24] ~ ~ ~ 0.95 0.82 0
damage @s 4.5 minecraft:generic by @a[tag=capskills_0128.double_lame.caster,limit=1]
effect give @s minecraft:weakness 3 0 true
effect give @s minecraft:glowing 3 0 true
title @a[tag=capskills_0128.double_lame.caster,limit=1] actionbar {"text":"Sentence exécutée — paire mixte.","color":"red"}
