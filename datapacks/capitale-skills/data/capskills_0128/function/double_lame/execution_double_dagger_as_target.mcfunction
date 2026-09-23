particle minecraft:damage_indicator ~ ~1.0 ~ 0.40 0.50 0.40 0.15 26 force @a[distance=..24]
particle minecraft:enchanted_hit ~ ~1.0 ~ 0.32 0.42 0.32 0.12 18 force @a[distance=..24]
playsound minecraft:entity.player.attack.crit player @a[distance=..24] ~ ~ ~ 1.15 0.62 0
damage @s 6 minecraft:generic by @a[tag=capskills_0128.double_lame.caster,limit=1]
effect give @s minecraft:weakness 5 1 true
effect give @s minecraft:slowness 4 1 true
effect give @s minecraft:glowing 4 0 true
title @a[tag=capskills_0128.double_lame.caster,limit=1] actionbar {"text":"EXÉCUTION CROISÉE — paire dague/rapière/griffe.","color":"dark_red","bold":true}
