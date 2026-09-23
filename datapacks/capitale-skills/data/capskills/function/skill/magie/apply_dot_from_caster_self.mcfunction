execute unless entity @e[tag=capskills.magic_dot_target,limit=1,sort=nearest] run title @s actionbar {"text":"Cible de Marque corrosive perdue.","color":"red"}
execute at @e[tag=capskills.magic_dot_target,limit=1,sort=nearest] run particle minecraft:dragon_breath ~ ~1.0 ~ 0.75 0.85 0.75 0.06 90 force @a[distance=..32]
execute at @e[tag=capskills.magic_dot_target,limit=1,sort=nearest] run particle minecraft:damage_indicator ~ ~1.0 ~ 0.35 0.45 0.35 0.05 18 force @a[distance=..32]
damage @e[tag=capskills.magic_dot_target,limit=1,sort=nearest] 3 minecraft:generic by @s
effect give @e[tag=capskills.magic_dot_target,limit=1,sort=nearest] minecraft:poison 8 0 true
effect give @e[tag=capskills.magic_dot_target,limit=1,sort=nearest] minecraft:glowing 6 0 true
scoreboard players set @s CAPSK_MAG_DOT_CD 10
execute at @e[tag=capskills.magic_dot_target,limit=1,sort=nearest] run playsound minecraft:entity.evoker.prepare_attack player @a[distance=..32] ~ ~ ~ 0.8 0.75 0
title @s actionbar {"text":"Marque corrosive appliquée. Recharge : 10 s.","color":"light_purple"}
