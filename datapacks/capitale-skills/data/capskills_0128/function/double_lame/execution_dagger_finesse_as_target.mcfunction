particle minecraft:damage_indicator ~ ~1.0 ~ 0.36 0.46 0.36 0.14 22 force @a[distance=..24]
particle minecraft:witch ~ ~1.0 ~ 0.20 0.35 0.20 0.02 8 force @a[distance=..24]
playsound minecraft:entity.player.attack.crit player @a[distance=..24] ~ ~ ~ 1.05 0.72 0
damage @s 5.5 minecraft:generic by @a[tag=capskills_0128.double_lame.caster,limit=1]
effect give @s minecraft:weakness 4 0 true
effect give @s minecraft:slowness 3 1 true
effect give @s minecraft:glowing 3 0 true
title @a[tag=capskills_0128.double_lame.caster,limit=1] actionbar {"text":"Sentence exécutée — dague/rapière/griffe et autre finesse.","color":"dark_red"}
