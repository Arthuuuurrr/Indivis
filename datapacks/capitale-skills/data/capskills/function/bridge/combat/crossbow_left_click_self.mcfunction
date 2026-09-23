# CapSkills 0.9.37 — clic gauche générique avec arbalète.
# Réservé aux futurs hooks de clic gauche dans le vide ; si appelé, prépare aussi le prochain carreau.
scoreboard players add @s CAPSK_CBOW_LCLK 1
execute if entity @s[tag=capskills.arbalete.vulnerabilite.r1] run function capskills:skill/arbalete/marque_vulnerabilite_start_self
execute unless entity @s[tag=capskills.arbalete.vulnerabilite.r1] if entity @s[tag=capskills.skill.trait_arbalete_marque_vulnerabilite_1] run function capskills:skill/arbalete/marque_vulnerabilite_start_self
