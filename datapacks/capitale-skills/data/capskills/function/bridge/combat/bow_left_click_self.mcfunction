# CapSkills 0.9.33 — hook neutre futur : clic gauche avec arc.
# Ancien comportement legacy supprimé : ne déclenche plus directement Rafale.
scoreboard players add @s CAPSK_BOW_LCLICK 1
# 0.9.57 : Flèche d’entrave préparée au clic gauche.
execute if entity @s[tag=capskills.trait.fleche_entrave.r1] run function capskills:skill/trait/fleche_entrave_start_self
execute unless entity @s[tag=capskills.trait.fleche_entrave.r1] if entity @s[tag=capskills.skill.trait_fleche_entrave_1] run function capskills:skill/trait/fleche_entrave_start_self
