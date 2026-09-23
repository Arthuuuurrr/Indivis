execute unless entity @e[tag=capskills.alter_target,limit=1,sort=nearest] run title @s actionbar {"text":"Cible de suspension perdue.","color":"red"}
execute at @e[tag=capskills.alter_target,limit=1,sort=nearest] run particle minecraft:end_rod ~ ~0.8 ~ 0.45 1.10 0.45 0.05 55 force @a[distance=..28]
execute at @e[tag=capskills.alter_target,limit=1,sort=nearest] run particle minecraft:witch ~ ~1.0 ~ 0.70 1.00 0.70 0.05 45 force @a[distance=..28]
effect give @e[tag=capskills.alter_target,limit=1,sort=nearest] minecraft:levitation 5 0 true
effect give @e[tag=capskills.alter_target,limit=1,sort=nearest] minecraft:glowing 5 0 true
scoreboard players set @s CAPSK_ALTER_LEV_CD 16
execute at @e[tag=capskills.alter_target,limit=1,sort=nearest] run playsound minecraft:block.amethyst_block.chime player @a[distance=..24] ~ ~ ~ 0.8 0.65 0
title @s actionbar {"text":"Suspension des Seuils appliquée (5 s). Recharge : 16 s.","color":"dark_green"}
# 0.8.30-rc4 : Entrave renforce aussi la Suspension des Seuils.
execute if entity @s[tag=capskills.alteration.entrave.r1] run effect give @e[tag=capskills.alter_target,limit=1,sort=nearest] minecraft:slowness 6 1 true
execute if entity @s[tag=capskills.alteration.entrave.r2] run effect give @e[tag=capskills.alter_target,limit=1,sort=nearest] minecraft:weakness 5 0 true
execute if entity @s[tag=capskills.alteration.entrave.r2] run effect give @e[tag=capskills.alter_target,limit=1,sort=nearest] minecraft:glowing 8 0 true
