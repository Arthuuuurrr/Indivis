# La recharge est posée avant les dégâts pour empêcher un second hook du même clic.
scoreboard players set @a[tag=capskills_0130.double_lame.passive_caster,limit=1] CAPSK_DUAL_PASS_CD 8
execute if entity @a[tag=capskills_0130.double_lame.passive_caster,tag=capskills_0130.double_lame.passive_mixed,limit=1] run damage @s 1 minecraft:generic by @a[tag=capskills_0130.double_lame.passive_caster,limit=1]
execute if entity @a[tag=capskills_0130.double_lame.passive_caster,tag=capskills_0130.double_lame.passive_matched,limit=1] run damage @s 1.5 minecraft:generic by @a[tag=capskills_0130.double_lame.passive_caster,limit=1]
execute if entity @a[tag=capskills_0130.double_lame.passive_caster,tag=capskills_0130.double_lame.passive_dagger_finesse,limit=1] run damage @s 2 minecraft:generic by @a[tag=capskills_0130.double_lame.passive_caster,limit=1]
execute if entity @a[tag=capskills_0130.double_lame.passive_caster,tag=capskills_0130.double_lame.passive_double_dagger,limit=1] run damage @s 2.5 minecraft:generic by @a[tag=capskills_0130.double_lame.passive_caster,limit=1]
particle minecraft:enchanted_hit ~ ~1.0 ~ 0.22 0.30 0.22 0.08 10 force @a[distance=..20]
playsound minecraft:entity.player.attack.crit player @a[distance=..20] ~ ~ ~ 0.45 1.35 0
effect give @s minecraft:weakness 2 0 true
