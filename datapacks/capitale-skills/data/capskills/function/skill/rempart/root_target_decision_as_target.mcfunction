
# 0.9.29 — @s = entité vivante ciblée par le raycast du Relais : joueur ou mob.
# Applique le root uniquement si le lanceur a le perk et si le cooldown est prêt.
scoreboard players set #hit CAPSK_TMP 1
scoreboard players set #can_root CAPSK_TMP 0
execute as @a[tag=capskills.caster,limit=1,sort=nearest] if score @s CAPSK_REMP_ROOT_CD matches 1.. run title @s actionbar [{"text":"Root du Rempart en recharge : ","color":"blue"},{"score":{"name":"@s","objective":"CAPSK_REMP_ROOT_CD"},"color":"blue"},{"text":" s","color":"blue"}]
execute as @a[tag=capskills.caster,limit=1,sort=nearest] if score @s CAPSK_REMP_ROOT_CD matches 0 run scoreboard players set #can_root CAPSK_TMP 1
execute if score #can_root CAPSK_TMP matches 1 run effect give @s minecraft:slowness 5 9 false
execute if score #can_root CAPSK_TMP matches 1 run effect give @s minecraft:glowing 5 0 true
execute if score #can_root CAPSK_TMP matches 1 run tag @s add capskills.rempart.rooted_visual
execute if score #can_root CAPSK_TMP matches 1 run scoreboard players set @s CAPSK_REMP_ROOT_VIS 100
execute if score #can_root CAPSK_TMP matches 1 run particle minecraft:crit ~ ~1.0 ~ 0.35 0.35 0.35 0.04 18 force @a[distance=..24]
execute if score #can_root CAPSK_TMP matches 1 run particle minecraft:electric_spark ~ ~1.55 ~ 0.22 0.20 0.22 0.01 8 force @a[distance=..24]
execute if score #can_root CAPSK_TMP matches 1 run playsound minecraft:block.chain.place player @a[distance=..20] ~ ~ ~ 0.55 0.75 0
execute if score #can_root CAPSK_TMP matches 1 as @a[tag=capskills.caster,limit=1,sort=nearest] run scoreboard players set @s CAPSK_REMP_ROOT_CD 22
execute if score #can_root CAPSK_TMP matches 1 as @a[tag=capskills.caster,limit=1,sort=nearest] run title @s actionbar {"text":"Root du Rempart déclenché : immobilisation 5 s. Recharge : 22 s.","color":"blue"}
