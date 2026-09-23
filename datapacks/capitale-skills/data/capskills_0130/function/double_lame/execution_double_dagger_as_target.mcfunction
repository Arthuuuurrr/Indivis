particle minecraft:damage_indicator ~ ~1.0 ~ 0.38 0.48 0.38 0.14 24 force @a[distance=..24]
particle minecraft:enchanted_hit ~ ~1.0 ~ 0.32 0.42 0.32 0.12 18 force @a[distance=..24]
playsound minecraft:entity.player.attack.crit player @a[distance=..24] ~ ~ ~ 1.05 0.72 0
damage @s 8 minecraft:generic by @a[tag=capskills_0130.double_lame.caster,limit=1]
effect give @s minecraft:weakness 5 1 true
effect give @s minecraft:slowness 4 1 true
effect give @s minecraft:glowing 4 0 true
title @a[tag=capskills_0130.double_lame.caster,limit=1] actionbar {"text":"EXÉCUTION CROISÉE — paire dague/rapière/griffe.","color":"dark_red","bold":true}
