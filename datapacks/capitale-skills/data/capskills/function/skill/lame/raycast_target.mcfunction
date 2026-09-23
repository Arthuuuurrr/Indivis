execute if score #hit CAPSK_TMP matches 0 run particle minecraft:sweep_attack ~ ~ ~ 0.15 0.15 0.15 0.00 1 force @a[distance=..16]
execute if score #hit CAPSK_TMP matches 0 as @e[distance=..1.35,tag=!capskills.lame_caster,sort=nearest] if data entity @s Health run function capskills:skill/lame/mark_target_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~0.65 ~ as @e[distance=..1.55,tag=!capskills.lame_caster,sort=nearest] if data entity @s Health run function capskills:skill/lame/mark_target_as_target
scoreboard players add @a[tag=capskills.lame_caster,limit=1,sort=nearest] CAPSK_RAY_STEP 1
execute if score #hit CAPSK_TMP matches 0 as @a[tag=capskills.lame_caster,limit=1,sort=nearest] rotated as @s if score @s CAPSK_RAY_STEP matches ..13 positioned ^ ^ ^0.45 run function capskills:skill/lame/raycast_target
