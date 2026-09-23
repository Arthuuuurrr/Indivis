# CapSkills 0.9.14 — Rafale : pseudo-hitbox plus juste.
# @s = lanceur via tag capskills.trait_caster ; position = point courant du rayon.
# Minecraft ne cible pas une vraie hitbox en commande : on sonde plusieurs hauteurs autour du rayon.
execute if score #hit CAPSK_TMP matches 0 run particle minecraft:crit ~ ~ ~ 0.030 0.030 0.030 0.00 1 force @a[distance=..32]
execute if score #hit CAPSK_TMP matches 0 as @e[distance=..1.45,tag=!capskills.trait_caster,sort=nearest,limit=1] if data entity @s Health run function capskills:skill/trait/mark_target_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~-0.55 ~ as @e[distance=..1.45,tag=!capskills.trait_caster,sort=nearest,limit=1] if data entity @s Health run function capskills:skill/trait/mark_target_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~-1.10 ~ as @e[distance=..1.45,tag=!capskills.trait_caster,sort=nearest,limit=1] if data entity @s Health run function capskills:skill/trait/mark_target_as_target
execute if score #hit CAPSK_TMP matches 0 positioned ~ ~0.55 ~ as @e[distance=..1.45,tag=!capskills.trait_caster,sort=nearest,limit=1] if data entity @s Health run function capskills:skill/trait/mark_target_as_target
scoreboard players add @a[tag=capskills.trait_caster,limit=1,sort=nearest] CAPSK_RAY_STEP 1
execute if score #hit CAPSK_TMP matches 0 as @a[tag=capskills.trait_caster,limit=1,sort=nearest] rotated as @s if score @s CAPSK_RAY_STEP matches ..84 positioned ^ ^ ^0.50 run function capskills:skill/trait/raycast_target
