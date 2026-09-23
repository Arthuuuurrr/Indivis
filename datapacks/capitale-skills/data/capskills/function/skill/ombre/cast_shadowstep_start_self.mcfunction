function capskills:integration/ensure_current_self
execute unless entity @s[tag=capskills.ombre.shadowstep.r1] run title @s actionbar {"text":"Pas déphasé non débloqué.","color":"red"}
execute if entity @s[tag=capskills.ombre.shadowstep.r1] if score @s CAPSK_OMBRESTEP_CD matches 1.. run title @s actionbar {"text":"Pas déphasé en recharge.","color":"dark_gray"}
execute if entity @s[tag=capskills.ombre.shadowstep.r1] if score @s CAPSK_OMBRESTEP_CD matches 0 run scoreboard players set #hit CAPSK_TMP 0
execute if entity @s[tag=capskills.ombre.shadowstep.r1] if score @s CAPSK_OMBRESTEP_CD matches 0 run scoreboard players set @s CAPSK_RAY_STEP 0
execute if entity @s[tag=capskills.ombre.shadowstep.r1] if score @s CAPSK_OMBRESTEP_CD matches 0 run tag @e[tag=capskills.ombre_target] remove capskills.ombre_target
execute if entity @s[tag=capskills.ombre.shadowstep.r1] if score @s CAPSK_OMBRESTEP_CD matches 0 run tag @s add capskills.shadow_caster
execute if entity @s[tag=capskills.ombre.shadowstep.r1] if score @s CAPSK_OMBRESTEP_CD matches 0 at @s run playsound minecraft:entity.enderman.teleport player @s ~ ~ ~ 0.35 1.75 0
execute if entity @s[tag=capskills.ombre.shadowstep.r1] if score @s CAPSK_OMBRESTEP_CD matches 0 at @s anchored eyes rotated as @s positioned ^ ^ ^0.75 run function capskills:skill/ombre/raycast_shadowstep_target
execute if entity @s[tag=capskills.ombre.shadowstep.r1] if score @s CAPSK_OMBRESTEP_CD matches 0 if score #hit CAPSK_TMP matches 1.. run function capskills:skill/ombre/apply_shadowstep_from_caster_self
execute if entity @s[tag=capskills.ombre.shadowstep.r1] if score @s CAPSK_OMBRESTEP_CD matches 0 if score #hit CAPSK_TMP matches 0 run title @s actionbar {"text":"Aucune cible pour Pas déphasé.","color":"yellow"}
tag @s remove capskills.shadow_caster
tag @e[tag=capskills.ombre_target] remove capskills.ombre_target
