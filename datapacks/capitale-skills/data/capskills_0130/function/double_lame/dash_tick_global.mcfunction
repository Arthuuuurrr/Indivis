# Tick global des ruées actives. Le contexte joueur est restauré pour chaque lanceur.
execute as @a[tag=capskills_0130.double_lame.dash_active] at @s run function capskills_0130:double_lame/dash_step_self
execute if entity @a[tag=capskills_0130.double_lame.dash_active] run schedule function capskills_0130:double_lame/dash_tick_global 1t replace
