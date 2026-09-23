# 0.9.35 — rattrapage discret des catalyseurs manquants après migration.
# Donne seulement une première fois, pour éviter de dupliquer les items à chaque sync.
execute if entity @s[tag=capskills.skill.commun_canalisation_vitale_1] unless entity @s[tag=capskills.item.baguette_soins_given] run function capskills:give/mod/baguette_soins_self
execute if entity @s[tag=capskills.skill.commun_canalisation_vitale_1] run tag @s add capskills.item.baguette_soins_given
execute if entity @s[tag=capskills.skill.secours_imperial_1] unless entity @s[tag=capskills.item.baguette_soins_given] run function capskills:give/mod/baguette_soins_self
execute if entity @s[tag=capskills.skill.secours_imperial_1] run tag @s add capskills.item.baguette_soins_given
execute if entity @s[tag=capskills.skill.rempart_imperial_1] unless entity @s[tag=capskills.item.relais_rempart_given] run function capskills:give/mod/relais_rempart_self
execute if entity @s[tag=capskills.skill.rempart_imperial_1] run tag @s add capskills.item.relais_rempart_given
execute if entity @s[tag=capskills.skill.magie_focus_coeur_1] unless entity @s[tag=capskills.item.baguette_destruction_given] run function capskills:give/mod/baguette_destruction_self
execute if entity @s[tag=capskills.skill.magie_focus_coeur_1] run tag @s add capskills.item.baguette_destruction_given
execute if entity @s[tag=capskills.skill.alteration_seuils_1] unless entity @s[tag=capskills.item.baguette_alteration_given] run function capskills:give/mod/baguette_alteration_self
execute if entity @s[tag=capskills.skill.alteration_seuils_1] run tag @s add capskills.item.baguette_alteration_given
execute if entity @s[tag=capskills.skill.ombre_voie_ombres_1] unless entity @s[tag=capskills.item.sceau_ombres_given] run function capskills:give/mod/sceau_ombres_self
execute if entity @s[tag=capskills.skill.ombre_voie_ombres_1] run tag @s add capskills.item.sceau_ombres_given
execute if entity @s[tag=capskills.skill.trait_reglementaire_1] unless entity @s[tag=capskills.item.training_bow_given] run function capskills:give/starter/training_bow_self
execute if entity @s[tag=capskills.skill.trait_reglementaire_1] run tag @s add capskills.item.training_bow_given
execute if entity @s[tag=capskills.skill.lame_imperiale_1] unless entity @s[tag=capskills.item.training_sword_given] run function capskills:give/starter/training_sword_self
execute if entity @s[tag=capskills.skill.lame_imperiale_1] run tag @s add capskills.item.training_sword_given
execute if entity @s[tag=capskills.skill.trait_arbalete_antarmure_1] unless entity @s[tag=capskills.item.training_crossbow_given] run function capskills:give/starter/training_crossbow_self
execute if entity @s[tag=capskills.skill.trait_arbalete_antarmure_1] run tag @s add capskills.item.training_crossbow_given

execute if entity @s[tag=capskills.skill.chaman_totem_protection_1] unless entity @s[tag=capskills.item.fetiche_ancetres_given] run function capskills:give/mod/fetiche_ancetres_self
execute if entity @s[tag=capskills.skill.chaman_totem_protection_1] run tag @s add capskills.item.fetiche_ancetres_given

# 0.9.39 : Fétiche pour Totem de Guérison.
execute if entity @s[tag=capskills.skill.chaman_totem_guerison_1] unless entity @s[tag=capskills.item.fetiche_ancetres_given] run function capskills:give/mod/fetiche_ancetres_self
execute if entity @s[tag=capskills.skill.chaman_totem_guerison_1] run tag @s add capskills.item.fetiche_ancetres_given

# 0.9.40 : Fétiche pour chaman_totem_givre_1.
execute if entity @s[tag=capskills.skill.chaman_totem_givre_1] unless entity @s[tag=capskills.item.fetiche_ancetres_given] run function capskills:give/mod/fetiche_ancetres_self
execute if entity @s[tag=capskills.skill.chaman_totem_givre_1] run tag @s add capskills.item.fetiche_ancetres_given
