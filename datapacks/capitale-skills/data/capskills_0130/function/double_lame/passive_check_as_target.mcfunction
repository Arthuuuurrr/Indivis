# Calcule le seuil de vie selon la paire. Health et max_health sont stockés x100.
scoreboard players set #dual_threshold CAPSK_DUAL_TMP 20
execute if entity @a[tag=capskills_0130.double_lame.passive_caster,tag=capskills_0130.double_lame.passive_matched,limit=1] run scoreboard players set #dual_threshold CAPSK_DUAL_TMP 25
execute if entity @a[tag=capskills_0130.double_lame.passive_caster,tag=capskills_0130.double_lame.passive_dagger_finesse,limit=1] run scoreboard players set #dual_threshold CAPSK_DUAL_TMP 30
execute if entity @a[tag=capskills_0130.double_lame.passive_caster,tag=capskills_0130.double_lame.passive_double_dagger,limit=1] run scoreboard players set #dual_threshold CAPSK_DUAL_TMP 35
execute store result score #dual_health CAPSK_DUAL_TMP run data get entity @s Health 100
execute store result score #dual_limit CAPSK_DUAL_TMP run attribute @s minecraft:max_health get 100
scoreboard players operation #dual_limit CAPSK_DUAL_TMP *= #dual_threshold CAPSK_DUAL_TMP
scoreboard players set #dual_hundred CAPSK_DUAL_TMP 100
scoreboard players operation #dual_limit CAPSK_DUAL_TMP /= #dual_hundred CAPSK_DUAL_TMP
execute if score #dual_health CAPSK_DUAL_TMP <= #dual_limit CAPSK_DUAL_TMP run function capskills_0130:double_lame/passive_apply_as_target
