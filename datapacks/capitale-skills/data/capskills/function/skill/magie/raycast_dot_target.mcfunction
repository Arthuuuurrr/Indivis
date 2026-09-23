execute if score #hit CAPSK_TMP matches 0 run particle minecraft:dragon_breath ~ ~ ~ 0.04 0.04 0.04 0.00 3 force @a[distance=..32]
execute if score #hit CAPSK_TMP matches 0 as @e[distance=..1.80,tag=!capskills.caster,sort=nearest] if data entity @s Health run function capskills:skill/magie/mark_dot_target_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~0.75 ~ as @e[distance=..1.95,tag=!capskills.caster,sort=nearest] if data entity @s Health run function capskills:skill/magie/mark_dot_target_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~-0.75 ~ as @e[distance=..1.95,tag=!capskills.caster,sort=nearest] if data entity @s Health run function capskills:skill/magie/mark_dot_target_as_target
scoreboard players add @a[tag=capskills.caster,limit=1,sort=nearest] CAPSK_RAY_STEP 1
execute if score #hit CAPSK_TMP matches 0 as @a[tag=capskills.caster,limit=1,sort=nearest] rotated as @s if score @s CAPSK_RAY_STEP matches ..68 positioned ^ ^ ^0.45 run function capskills:skill/magie/raycast_dot_target
