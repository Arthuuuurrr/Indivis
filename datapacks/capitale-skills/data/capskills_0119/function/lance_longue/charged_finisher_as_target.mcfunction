scoreboard players add @a[tag=capskills_0119.lance_longue.caster,limit=1] CAPSK_LANCE_HITS 1
scoreboard players set #lance_hit CAPSK_LANCE_RAY 1
particle minecraft:enchanted_hit ~ ~1.0 ~ 0.45 0.50 0.45 0.16 24 force @a[distance=..28]
particle minecraft:sweep_attack ~ ~1.0 ~ 0.15 0.15 0.15 0.01 2 force @a[distance=..28]
particle minecraft:damage_indicator ~ ~1.0 ~ 0.32 0.42 0.32 0.10 15 force @a[distance=..28]
playsound minecraft:item.trident.hit player @a[distance=..28] ~ ~ ~ 1.00 0.70 0
playsound minecraft:entity.player.attack.knockback player @a[distance=..28] ~ ~ ~ 0.90 0.80 0
damage @s 2 minecraft:generic by @a[tag=capskills_0119.lance_longue.caster,limit=1]
effect give @s minecraft:glowing 1 0 true
