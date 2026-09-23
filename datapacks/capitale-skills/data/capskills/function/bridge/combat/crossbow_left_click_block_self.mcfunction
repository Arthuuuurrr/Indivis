# CapSkills 0.9.58 — clic gauche arbalète sur bloc : pas de cible directe.
function capskills:integration/ensure_current_self
execute if entity @s[tag=capskills.arbalete.vulnerabilite.r1] run title @s actionbar {"text":"Marque/Rupture : clic gauche sur une cible vivante.","color":"yellow"}
execute unless entity @s[tag=capskills.arbalete.vulnerabilite.r1] unless entity @s[tag=capskills.skill.trait_arbalete_marque_vulnerabilite_1] run title @s actionbar {"text":"Marque de vulnérabilité non débloquée.","color":"red"}
