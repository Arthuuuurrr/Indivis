scoreboard players add @a[tag=capskills_0119.lance_longue.caster,limit=1] CAPSK_LANCE_HITS 1
scoreboard players set #lance_hit CAPSK_LANCE_RAY 1
particle minecraft:crit ~ ~1.0 ~ 0.25 0.35 0.25 0.08 12 force @a[distance=..24]
particle minecraft:damage_indicator ~ ~1.0 ~ 0.15 0.25 0.15 0.05 6 force @a[distance=..24]
playsound bettercombat:spear_stab player @a[distance=..24] ~ ~ ~ 0.52 1.18 0
damage @s 1.75 minecraft:generic by @a[tag=capskills_0119.lance_longue.caster,limit=1]
effect give @s minecraft:slowness 1 0 true
effect give @s minecraft:glowing 1 0 true
