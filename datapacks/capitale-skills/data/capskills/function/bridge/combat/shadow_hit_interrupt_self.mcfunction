# CapSkills 0.9.57 — hit avec le Sceau des Ombres : Coup d'arrêt et/ou Frappe de sortie.
# @s = joueur ; cible temporaire taggée par le jar : capskills.mod_hit_target.
function capskills:integration/ensure_current_self
execute unless entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run title @s actionbar {"text":"Aucune cible pour le Sceau des Ombres.","color":"yellow"}

# Coup d'arrêt existant.
execute unless entity @s[tag=capskills.ombre.interrupt.r1] unless entity @s[tag=capskills.skill.ombre_interrupt_dephase_1] unless entity @s[tag=capskills.ombre.sortie.ready] run title @s actionbar {"text":"Coup d'arrêt non débloqué.","color":"red"}
execute if entity @s[tag=capskills.ombre.interrupt.r1] if score @s CAPSK_OMBRE_INT_CD matches 1.. run title @s actionbar [{"text":"Coup d'arrêt en recharge : ","color":"dark_gray"},{"score":{"name":"@s","objective":"CAPSK_OMBRE_INT_CD"},"color":"dark_gray"},{"text":" s","color":"dark_gray"}]
execute if entity @s[tag=capskills.ombre.interrupt.r1] if score @s CAPSK_OMBRE_INT_CD matches 0 if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run tag @s add capskills.shadow_caster
execute if entity @s[tag=capskills.ombre.interrupt.r1] if score @s CAPSK_OMBRE_INT_CD matches 0 if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] as @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] at @s run function capskills:skill/ombre/interrupt_target_as_target
execute if entity @s[tag=capskills.shadow_caster] run scoreboard players set @s CAPSK_OMBRE_INT_CD 16

# Frappe de sortie : effet de contrôle court si Pas déphasé vient d'être utilisé.
execute if entity @s[tag=capskills.ombre.frappe_sortie.r1,tag=capskills.ombre.sortie.ready] if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run tag @s add capskills.shadow_caster
execute if entity @s[tag=capskills.ombre.frappe_sortie.r1,tag=capskills.ombre.sortie.ready] if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] as @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] at @s run function capskills:skill/ombre/frappe_sortie_apply_as_target
execute if entity @s[tag=capskills.ombre.frappe_sortie.r1,tag=capskills.ombre.sortie.ready] run tag @s remove capskills.ombre.sortie.ready
execute if entity @s[tag=capskills.ombre.frappe_sortie.r1] run scoreboard players set @s CAPSK_OMBRE_SORTIE_T 0

tag @s remove capskills.shadow_caster
