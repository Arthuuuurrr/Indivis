# Exécutée depuis la position d'arrivée, avec la cible comme @s.
tp @a[tag=capskills_0130.double_lame.caster,limit=1] ~ ~ ~ facing entity @s eyes
tag @a[tag=capskills_0130.double_lame.caster,limit=1] add capskills_0130.double_lame.dash_done
particle minecraft:poof ~ ~0.9 ~ 0.20 0.35 0.20 0.03 12 force @a[distance=..24]
particle minecraft:sweep_attack ~ ~1.0 ~ 0.08 0.08 0.08 0.00 1 force @a[distance=..24]
playsound minecraft:entity.enderman.teleport player @a[distance=..24] ~ ~ ~ 0.30 1.62 0
