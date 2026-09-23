# CapSkills 0.9.58 — clic gauche arbalète sur entité : Marque/Rupture directes.
# @s = arbalétrier ; cible temporaire taggée par le jar : capskills.mod_hit_target.
function capskills:integration/ensure_current_self
scoreboard players add @s CAPSK_ARBA_MARK_CD 0
scoreboard players add @s CAPSK_ARBA_MARK_T 0
execute unless entity @s[tag=capskills.arbalete.vulnerabilite.r1] unless entity @s[tag=capskills.skill.trait_arbalete_marque_vulnerabilite_1] run title @s actionbar {"text":"Marque de vulnérabilité non débloquée.","color":"red"}
execute if entity @s[tag=capskills.arbalete.vulnerabilite.r1] if score @s CAPSK_ARBA_MARK_CD matches 1.. run title @s actionbar [{"text":"Marque/Rupture en recharge : ","color":"yellow"},{"score":{"name":"@s","objective":"CAPSK_ARBA_MARK_CD"},"color":"yellow"},{"text":" s","color":"yellow"}]
execute if entity @s[tag=capskills.arbalete.vulnerabilite.r1] if score @s CAPSK_ARBA_MARK_CD matches 0 unless entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run title @s actionbar {"text":"Arbalète : aucune cible.","color":"yellow"}
execute if entity @s[tag=capskills.arbalete.vulnerabilite.r1] if score @s CAPSK_ARBA_MARK_CD matches 0 if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run tag @s add capskills.arbalete_caster
execute if entity @s[tag=capskills.arbalete.vulnerabilite.r1] if score @s CAPSK_ARBA_MARK_CD matches 0 if entity @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] as @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] at @s run function capskills:skill/arbalete/marque_vulnerabilite_impact_as_target
execute if entity @s[tag=capskills.arbalete.rupture.r1] if entity @s[tag=capskills.arbalete_caster] as @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] at @s run function capskills:skill/arbalete/carreau_rupture_impact_as_target
execute if entity @s[tag=capskills.arbalete_caster] run scoreboard players set @s CAPSK_ARBA_MARK_CD 18
execute if entity @s[tag=capskills.arbalete_caster] run title @s actionbar {"text":"Marque/Rupture appliquée.","color":"yellow"}
tag @s remove capskills.arbalete_caster
