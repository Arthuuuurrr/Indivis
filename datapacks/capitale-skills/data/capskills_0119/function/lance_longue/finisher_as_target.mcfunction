scoreboard players add @a[tag=capskills_0119.lance_longue.caster,limit=1] CAPSK_LANCE_HITS 1
scoreboard players set #lance_hit CAPSK_LANCE_RAY 1
particle minecraft:enchanted_hit ~ ~1.0 ~ 0.35 0.45 0.35 0.12 18 force @a[distance=..24]
particle minecraft:damage_indicator ~ ~1.0 ~ 0.28 0.38 0.28 0.08 12 force @a[distance=..24]
playsound minecraft:entity.player.attack.crit player @a[distance=..24] ~ ~ ~ 0.85 0.72 0
damage @s 3 minecraft:generic by @a[tag=capskills_0119.lance_longue.caster,limit=1]
effect give @s minecraft:weakness 2 0 true
effect give @s minecraft:slowness 2 1 true
effect give @s minecraft:glowing 2 0 true
