# Résout la cible appartenant à ce lanceur avec CAPSK_UID / CAPSK_DASH_OWNER.
tag @e[tag=capskills_0130.double_lame.dash_target_current] remove capskills_0130.double_lame.dash_target_current
tag @s add capskills_0130.double_lame.dash_caster_context
execute as @e[tag=capskills_0130.double_lame.dash_target] if score @s CAPSK_DASH_OWNER = @a[tag=capskills_0130.double_lame.dash_caster_context,limit=1] CAPSK_UID run tag @s add capskills_0130.double_lame.dash_target_current
tag @s remove capskills_0130.double_lame.dash_caster_context

# Arrêt si la cible a disparu, est hors portée ou est déjà suffisamment proche.
execute unless entity @e[tag=capskills_0130.double_lame.dash_target_current,sort=nearest,limit=1,distance=..5.75] run function capskills_0130:double_lame/dash_stop_self
execute if entity @e[tag=capskills_0130.double_lame.dash_target_current,sort=nearest,limit=1,distance=..1.20] run function capskills_0130:double_lame/dash_stop_self

# Orientation vers la cible avant chaque impulsion.
execute if entity @s[tag=capskills_0130.double_lame.dash_active] run tp @s ~ ~ ~ facing entity @e[tag=capskills_0130.double_lame.dash_target_current,sort=nearest,limit=1] eyes

# Ordre inverse : un changement de tag ne peut pas déclencher deux étapes le même tick.
execute if entity @s[tag=capskills_0130.double_lame.dash_step_4] run function capskills_0130:double_lame/dash_step_4_self
execute if entity @s[tag=capskills_0130.double_lame.dash_step_3] run function capskills_0130:double_lame/dash_step_3_self
execute if entity @s[tag=capskills_0130.double_lame.dash_step_2] run function capskills_0130:double_lame/dash_step_2_self
execute if entity @s[tag=capskills_0130.double_lame.dash_step_1] run function capskills_0130:double_lame/dash_step_1_self
execute if entity @s[tag=capskills_0130.double_lame.dash_step_0] run function capskills_0130:double_lame/dash_step_0_self

tag @e[tag=capskills_0130.double_lame.dash_target_current] remove capskills_0130.double_lame.dash_target_current
