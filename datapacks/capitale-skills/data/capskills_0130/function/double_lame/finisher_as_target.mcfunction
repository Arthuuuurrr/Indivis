# Sentence croisée II — troisième et dernier impact hors exécution.
particle minecraft:enchanted_hit ~ ~1.0 ~ 0.34 0.42 0.34 0.14 20 force @a[distance=..24]
particle minecraft:damage_indicator ~ ~1.0 ~ 0.20 0.28 0.20 0.08 8 force @a[distance=..24]
playsound minecraft:entity.player.attack.crit player @a[distance=..24] ~ ~ ~ 0.90 0.92 0

execute if entity @a[tag=capskills_0130.double_lame.caster,tag=capskills_0130.double_lame.synergy_mixed,limit=1] run damage @s 4 minecraft:generic by @a[tag=capskills_0130.double_lame.caster,limit=1]
execute if entity @a[tag=capskills_0130.double_lame.caster,tag=capskills_0130.double_lame.synergy_matched,limit=1] run damage @s 4.5 minecraft:generic by @a[tag=capskills_0130.double_lame.caster,limit=1]
execute if entity @a[tag=capskills_0130.double_lame.caster,tag=capskills_0130.double_lame.synergy_dagger_finesse,limit=1] run damage @s 5 minecraft:generic by @a[tag=capskills_0130.double_lame.caster,limit=1]
execute if entity @a[tag=capskills_0130.double_lame.caster,tag=capskills_0130.double_lame.synergy_double_dagger,limit=1] run damage @s 5.5 minecraft:generic by @a[tag=capskills_0130.double_lame.caster,limit=1]

effect give @s minecraft:slowness 2 0 true
effect give @s minecraft:glowing 2 0 true
