function capskills_0130:double_lame/dash_stop_self
tag @s remove capskills_0130.double_lame.channeling
tag @s remove capskills_0130.double_lame.active
tag @s remove capskills_0130.double_lame.caster
tag @s remove capskills_0130.double_lame.synergy_mixed
tag @s remove capskills_0130.double_lame.synergy_matched
tag @s remove capskills_0130.double_lame.synergy_dagger_finesse
tag @s remove capskills_0130.double_lame.synergy_double_dagger
execute at @s run particle minecraft:cloud ~ ~0.7 ~ 0.32 0.12 0.32 0.01 6 force @a[distance=..20]
tag @s remove capskills_0130.double_lame.dash_done
tag @s remove capskills_0130.double_lame.dash_active
tag @s remove capskills_0130.double_lame.dash_step_0
tag @s remove capskills_0130.double_lame.dash_step_1
tag @s remove capskills_0130.double_lame.dash_step_2
tag @s remove capskills_0130.double_lame.dash_step_3
tag @s remove capskills_0130.double_lame.dash_step_4
tag @s remove capskills_0130.double_lame.dash_moved
tag @s remove capskills_0130.double_lame.dash_caster_context
