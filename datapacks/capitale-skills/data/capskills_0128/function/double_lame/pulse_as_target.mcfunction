particle minecraft:crit ~ ~1.0 ~ 0.22 0.30 0.22 0.10 10 force @a[distance=..24]
particle minecraft:sweep_attack ~ ~1.0 ~ 0.05 0.05 0.05 0.00 1 force @a[distance=..24]
playsound bettercombat:dagger_slash player @a[distance=..24] ~ ~ ~ 0.55 1.20 0
damage @s 1.5 minecraft:generic by @a[tag=capskills_0128.double_lame.caster,limit=1]
effect give @s minecraft:glowing 1 0 true
