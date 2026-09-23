# Nettoie uniquement la cible possédée par ce lanceur.
tag @s add capskills_0130.double_lame.dash_caster_context
execute as @e[tag=capskills_0130.double_lame.dash_target] if score @s CAPSK_DASH_OWNER = @a[tag=capskills_0130.double_lame.dash_caster_context,limit=1] CAPSK_UID run function capskills_0130:double_lame/dash_cleanup_target_as_target
tag @s remove capskills_0130.double_lame.dash_caster_context

tag @s remove capskills_0130.double_lame.dash_active
tag @s remove capskills_0130.double_lame.dash_step_0
tag @s remove capskills_0130.double_lame.dash_step_1
tag @s remove capskills_0130.double_lame.dash_step_2
tag @s remove capskills_0130.double_lame.dash_step_3
tag @s remove capskills_0130.double_lame.dash_step_4
tag @s remove capskills_0130.double_lame.dash_moved
