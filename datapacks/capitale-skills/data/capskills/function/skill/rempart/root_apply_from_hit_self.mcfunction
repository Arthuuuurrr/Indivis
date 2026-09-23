# CapSkills 0.9.51 — Root du Rempart appliqué depuis clic gauche / hit avec le Relais.
# @s = joueur tank ; la cible touchée porte capskills.mod_hit_target pendant l'appel Java.
execute unless entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run title @s actionbar {"text":"Root du Rempart : aucune cible valide.","color":"red"}
execute if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run effect give @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] minecraft:slowness 5 9 false
execute if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run effect give @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] minecraft:glowing 5 0 true
execute as @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run tag @s add capskills.rempart.rooted_visual
execute as @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run scoreboard players set @s CAPSK_REMP_ROOT_VIS 100
execute at @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run particle minecraft:crit ~ ~1.0 ~ 0.35 0.35 0.35 0.04 18 force @a[distance=..24]
execute at @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run particle minecraft:electric_spark ~ ~1.55 ~ 0.22 0.20 0.22 0.01 8 force @a[distance=..24]
execute at @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run playsound minecraft:block.chain.place player @a[distance=..20] ~ ~ ~ 0.55 0.75 0
execute if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run scoreboard players set @s CAPSK_REMP_ROOT_CD 22
execute if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run title @s actionbar {"text":"Root du Rempart déclenché : immobilisation 5 s. Recharge : 22 s.","color":"blue"}
