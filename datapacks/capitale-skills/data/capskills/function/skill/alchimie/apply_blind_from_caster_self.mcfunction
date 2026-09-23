execute unless entity @e[tag=capskills.alter_target,limit=1,sort=nearest] run title @s actionbar {"text":"Cible d’altération perdue.","color":"red"}
execute at @e[tag=capskills.alter_target,limit=1,sort=nearest] run particle minecraft:smoke ~ ~1.0 ~ 0.70 0.80 0.70 0.04 45 force @a[distance=..28]
execute at @e[tag=capskills.alter_target,limit=1,sort=nearest] run particle minecraft:witch ~ ~1.0 ~ 0.90 0.95 0.90 0.06 70 force @a[distance=..28]
effect give @e[tag=capskills.alter_target,limit=1,sort=nearest] minecraft:blindness 5 0 true
effect give @e[tag=capskills.alter_target,limit=1,sort=nearest] minecraft:slowness 4 0 true
scoreboard players set @s CAPSK_ALTER_TARGET_CD 12
execute at @e[tag=capskills.alter_target,limit=1,sort=nearest] run playsound minecraft:entity.illusioner.cast_spell player @a[distance=..24] ~ ~ ~ 0.65 0.8 0
title @s actionbar {"text":"Aveuglement des Seuils appliqué. Recharge : 12 s.","color":"dark_green"}
# 0.8.30-rc4 : Entrave devient une vraie amélioration d’Altération.
execute if entity @s[tag=capskills.alteration.entrave.r1] run effect give @e[tag=capskills.alter_target,limit=1,sort=nearest] minecraft:slowness 6 1 true
execute if entity @s[tag=capskills.alteration.entrave.r1] run effect give @e[tag=capskills.alter_target,limit=1,sort=nearest] minecraft:mining_fatigue 4 0 true
execute if entity @s[tag=capskills.alteration.entrave.r2] run effect give @e[tag=capskills.alter_target,limit=1,sort=nearest] minecraft:weakness 5 0 true
execute if entity @s[tag=capskills.alteration.entrave.r2] run effect give @e[tag=capskills.alter_target,limit=1,sort=nearest] minecraft:glowing 8 0 true
