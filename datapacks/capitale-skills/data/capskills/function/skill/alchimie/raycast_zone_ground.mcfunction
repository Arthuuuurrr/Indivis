# Raycast de zone : pose la zone sur le premier bloc solide regardé, un bloc au-dessus.
execute if score #block CAPSK_TMP matches 0 unless block ~ ~ ~ minecraft:air unless block ~ ~ ~ minecraft:cave_air unless block ~ ~ ~ minecraft:void_air run scoreboard players set #block CAPSK_TMP 1
execute if score #block CAPSK_TMP matches 1 as @a[tag=capskills.zone_caster,limit=1,sort=nearest] positioned ~ ~1 ~ run function capskills:skill/alchimie/apply_zone_here_self
scoreboard players add @a[tag=capskills.zone_caster,limit=1,sort=nearest] CAPSK_RAY_STEP 1
execute if score #block CAPSK_TMP matches 0 as @a[tag=capskills.zone_caster,limit=1,sort=nearest] rotated as @s if score @s CAPSK_RAY_STEP matches ..24 positioned ^ ^ ^0.75 run function capskills:skill/alchimie/raycast_zone_ground
