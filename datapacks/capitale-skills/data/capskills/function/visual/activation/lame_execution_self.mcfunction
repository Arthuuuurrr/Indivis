# CapSkills 0.9.9 — activation épée / Sentence d’exécution.
execute at @e[tag=capskills.lame_target,limit=1,sort=nearest] run particle minecraft:sweep_attack ~ ~1.0 ~ 0.10 0.10 0.10 0.00 2 force @a[distance=..28]
execute at @e[tag=capskills.lame_target,limit=1,sort=nearest] run particle minecraft:crit ~ ~1.05 ~ 0.38 0.25 0.38 0.04 18 force @a[distance=..28]
execute at @s run playsound minecraft:item.trident.hit player @a[distance=..28] ~ ~ ~ 0.42 0.85 0
