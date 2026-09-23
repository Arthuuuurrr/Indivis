# @s = lanceur ; cible = capskills.ombre_target.
execute unless entity @e[tag=capskills.ombre_target,limit=1,sort=nearest] run title @s actionbar {"text":"Cible de Pas déphasé perdue.","color":"red"}
execute at @s run function capskills:visual/activation/shadowstep_self
execute as @e[tag=capskills.ombre_target,limit=1,sort=nearest] at @s rotated as @s positioned ^ ^ ^-2.15 run tp @a[tag=capskills.shadow_caster,limit=1,sort=nearest] ~ ~ ~ facing entity @s eyes
execute at @a[tag=capskills.shadow_caster,limit=1,sort=nearest] run function capskills:visual/activation/shadowstep_self
execute at @e[tag=capskills.ombre_target,limit=1,sort=nearest] run playsound minecraft:entity.enderman.teleport player @a[distance=..24] ~ ~ ~ 0.45 1.55 0
execute if entity @s[tag=capskills.ombre.shadowstep.r2] as @e[tag=capskills.ombre_target,limit=1,sort=nearest,type=!minecraft:player] run effect give @s minecraft:slowness 3 0 true
execute if entity @s[tag=capskills.ombre.shadowstep.r2] as @e[tag=capskills.ombre_target,limit=1,sort=nearest,type=!minecraft:player] run effect give @s minecraft:glowing 3 0 true

execute if entity @s[tag=capskills.ombre.frappe_sortie.r1] run tag @s add capskills.ombre.sortie.ready
execute if entity @s[tag=capskills.ombre.frappe_sortie.r1] run scoreboard players set @s CAPSK_OMBRE_SORTIE_T 7
# 0.9.37 : Coup d’arrêt n’est plus déclenché automatiquement par Pas déphasé.
# Il se déclenche maintenant sur hit/clic gauche avec le Sceau des Ombres.
scoreboard players set @s CAPSK_OMBRESTEP_CD 18
execute if entity @s[tag=capskills.ombre.shadowstep.r2] run scoreboard players set @s CAPSK_OMBRESTEP_CD 14
execute unless entity @s[tag=capskills.ombre.shadowstep.r2] run title @s actionbar {"text":"Pas déphasé exécuté. Recharge : 18 s.","color":"dark_gray"}
execute if entity @s[tag=capskills.ombre.shadowstep.r2] run title @s actionbar {"text":"Pas déphasé II exécuté. Recharge : 14 s.","color":"dark_gray"}
