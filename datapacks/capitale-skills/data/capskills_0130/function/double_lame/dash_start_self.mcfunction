# Initialise une seule ruée par lancement et mémorise sa cible pour les ticks suivants.
execute unless score @s CAPSK_UID matches 1.. run function capskills:integration/assign_uid_self
tag @s add capskills_0130.double_lame.dash_caster_context
execute as @e[tag=capskills_0119.lance_longue.native_target,sort=nearest,limit=1] run function capskills_0130:double_lame/dash_mark_target_as_target
tag @s remove capskills_0130.double_lame.dash_caster_context

tag @s add capskills_0130.double_lame.dash_done
tag @s add capskills_0130.double_lame.dash_active
tag @s add capskills_0130.double_lame.dash_step_0
tag @s remove capskills_0130.double_lame.dash_step_1
tag @s remove capskills_0130.double_lame.dash_step_2
tag @s remove capskills_0130.double_lame.dash_step_3
tag @s remove capskills_0130.double_lame.dash_step_4
tag @s remove capskills_0130.double_lame.dash_moved
execute at @s run particle minecraft:sweep_attack ~ ~0.95 ~ 0.05 0.05 0.05 0 1 force @a[distance=..24]
execute at @s run playsound minecraft:entity.player.attack.sweep player @a[distance=..24] ~ ~ ~ 0.55 1.35 0
function capskills_0130:double_lame/dash_step_self
execute if entity @s[tag=capskills_0130.double_lame.dash_active] run schedule function capskills_0130:double_lame/dash_tick_global 1t replace
