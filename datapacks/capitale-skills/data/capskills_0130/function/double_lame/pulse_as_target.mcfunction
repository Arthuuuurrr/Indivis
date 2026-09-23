# Sentence croisée II — impact intermédiaire lourd.
particle minecraft:crit ~ ~1.0 ~ 0.22 0.30 0.22 0.10 14 force @a[distance=..24]
particle minecraft:sweep_attack ~ ~1.0 ~ 0.05 0.05 0.05 0.00 1 force @a[distance=..24]
playsound bettercombat:dagger_slash player @a[distance=..24] ~ ~ ~ 0.65 1.08 0

# Dégâts par frappe augmentés puisque la séquence ne comporte plus que trois impacts.
execute if entity @a[tag=capskills_0130.double_lame.caster,tag=capskills_0130.double_lame.synergy_mixed,limit=1] run damage @s 2.5 minecraft:generic by @a[tag=capskills_0130.double_lame.caster,limit=1]
execute if entity @a[tag=capskills_0130.double_lame.caster,tag=capskills_0130.double_lame.synergy_matched,limit=1] run damage @s 2.75 minecraft:generic by @a[tag=capskills_0130.double_lame.caster,limit=1]
execute if entity @a[tag=capskills_0130.double_lame.caster,tag=capskills_0130.double_lame.synergy_dagger_finesse,limit=1] run damage @s 3 minecraft:generic by @a[tag=capskills_0130.double_lame.caster,limit=1]
execute if entity @a[tag=capskills_0130.double_lame.caster,tag=capskills_0130.double_lame.synergy_double_dagger,limit=1] run damage @s 3.25 minecraft:generic by @a[tag=capskills_0130.double_lame.caster,limit=1]

effect give @s minecraft:glowing 1 0 true
