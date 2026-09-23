particle minecraft:enchanted_hit ~ ~1.0 ~ 0.30 0.38 0.30 0.12 16 force @a[distance=..24]
playsound minecraft:entity.player.attack.crit player @a[distance=..24] ~ ~ ~ 0.75 1.05 0
damage @s 2.5 minecraft:generic by @a[tag=capskills_0128.double_lame.caster,limit=1]
effect give @s minecraft:slowness 2 0 true
effect give @s minecraft:glowing 2 0 true
