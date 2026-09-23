execute if score #hit CAPSK_TMP matches 0 run particle minecraft:sculk_soul ~ ~ ~ 0.025 0.025 0.025 0.00 1 force @a[distance=..24]
execute if score #hit CAPSK_TMP matches 0 as @e[distance=..1.45,tag=!capskills.shadow_caster,sort=nearest] if data entity @s Health run function capskills:skill/ombre/mark_shadowstep_target_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~0.70 ~ as @e[distance=..1.65,tag=!capskills.shadow_caster,sort=nearest] if data entity @s Health run function capskills:skill/ombre/mark_shadowstep_target_as_target
scoreboard players add @a[tag=capskills.shadow_caster,limit=1,sort=nearest] CAPSK_RAY_STEP 1
execute if score #hit CAPSK_TMP matches 0 as @a[tag=capskills.shadow_caster,limit=1,sort=nearest] rotated as @s if score @s CAPSK_RAY_STEP matches ..62 positioned ^ ^ ^0.50 run function capskills:skill/ombre/raycast_shadowstep_target
