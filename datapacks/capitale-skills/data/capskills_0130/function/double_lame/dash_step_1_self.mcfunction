# Impulsion 2/5 — 0.55 bloc(s).
tag @s remove capskills_0130.double_lame.dash_moved
execute facing entity @e[tag=capskills_0130.double_lame.dash_target_current,sort=nearest,limit=1] feet rotated ~ 0 positioned ^ ^ ^0.55 if block ~ ~ ~ #capskills_0130:dash_passable if block ~ ~1 ~ #capskills_0130:dash_passable run tag @s add capskills_0130.double_lame.dash_moved
execute if entity @s[tag=capskills_0130.double_lame.dash_moved] facing entity @e[tag=capskills_0130.double_lame.dash_target_current,sort=nearest,limit=1] feet rotated ~ 0 positioned ^ ^ ^0.55 run tp @s ~ ~ ~ facing entity @e[tag=capskills_0130.double_lame.dash_target_current,sort=nearest,limit=1] eyes
execute unless entity @s[tag=capskills_0130.double_lame.dash_moved] run function capskills_0130:double_lame/dash_stop_self
execute if entity @s[tag=capskills_0130.double_lame.dash_moved] at @s run particle minecraft:cloud ~ ~0.12 ~ 0.10 0.03 0.10 0.015 3 force @a[distance=..24]
execute if entity @s[tag=capskills_0130.double_lame.dash_moved] run tag @s remove capskills_0130.double_lame.dash_step_1
execute if entity @s[tag=capskills_0130.double_lame.dash_moved] run tag @s add capskills_0130.double_lame.dash_step_2
tag @s remove capskills_0130.double_lame.dash_moved
