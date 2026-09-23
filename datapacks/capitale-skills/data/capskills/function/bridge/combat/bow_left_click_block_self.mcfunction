# CapSkills 0.9.58 — clic gauche arc sur bloc : pas de cible directe.
function capskills:integration/ensure_current_self
execute if entity @s[tag=capskills.trait.fleche_entrave.r1] run title @s actionbar {"text":"Flèche d’entrave : clic gauche sur une cible vivante.","color":"yellow"}
execute unless entity @s[tag=capskills.trait.fleche_entrave.r1] unless entity @s[tag=capskills.skill.trait_fleche_entrave_1] run title @s actionbar {"text":"Flèche d’entrave non débloquée.","color":"red"}
