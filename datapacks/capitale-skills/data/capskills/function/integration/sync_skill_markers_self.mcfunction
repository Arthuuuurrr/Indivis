# CapSkills 0.8.30-rc1 — migration légère des anciens tags vers les marqueurs de skill UI.
# N’ajoute aucune mécanique : permet seulement au profil/debug de détecter les skills déjà acquis avant 0.8.30.
execute if entity @s[tag=capskills.alchimiste.kit.r1] run tag @s add capskills.skill.alteration_alchimiste_1
execute if entity @s[tag=capskills.alchimiste.kit.r2] run tag @s add capskills.skill.alteration_alchimiste_2
execute if entity @s[tag=capskills.alteration.blind.r1] run tag @s add capskills.skill.alteration_aveuglement_1
execute if entity @s[tag=capskills.alteration.formules.r1] run tag @s add capskills.skill.alteration_formules_entrave_1
execute if entity @s[tag=capskills.alchimie.instable.r1] run tag @s add capskills.skill.alteration_instable_1
execute if entity @s[tag=capskills.alchimie.instable.r2] run tag @s add capskills.skill.alteration_instable_2
execute if entity @s[tag=capskills.alchimie.r2] run tag @s add capskills.skill.alteration_preparatoire_1
execute if entity @s[tag=capskills.alchimie.r3] run tag @s add capskills.skill.alteration_preparatoire_2
execute if entity @s[tag=capskills.alchimie.r1] run tag @s add capskills.skill.alteration_seuils_1
execute if entity @s[tag=capskills.alteration.stabilisation.r1] run tag @s add capskills.skill.alteration_stabilisation_seuil_1
execute if entity @s[tag=capskills.alteration.levitate.r1] run tag @s add capskills.skill.alteration_suspension_1
execute if entity @s[tag=capskills.farming.r1] run tag @s add capskills.skill.artisanat_agriculteur_1
execute if entity @s[tag=capskills.farming.r2] run tag @s add capskills.skill.artisanat_agriculteur_2
execute if entity @s[tag=capskills.forge.r1] run tag @s add capskills.skill.artisanat_forgeron_1
execute if entity @s[tag=capskills.forge.r2] run tag @s add capskills.skill.artisanat_forgeron_2
execute if entity @s[tag=capskills.artisanat.socle.r1] run tag @s add capskills.skill.artisanat_socle_1
execute if entity @s[tag=capskills.commun.selfheal] run tag @s add capskills.skill.commun_canalisation_vitale_1
execute if entity @s[tag=capskills.enchanteur.r1] run tag @s add capskills.skill.enchanteur_1
execute if entity @s[tag=capskills.enchanteur.r2] run tag @s add capskills.skill.enchanteur_2
execute if entity @s[tag=capskills.enchanteur.artisan.r1] run tag @s add capskills.skill.enchanteur_main_artisan_1
execute if entity @s[tag=capskills.lame.breche.r1] run tag @s add capskills.skill.lame_breche_execution_1
execute if entity @s[tag=capskills.lame.discipline.r1] run tag @s add capskills.skill.lame_discipline_fer_1
execute if entity @s[tag=capskills.lame.fente.r1] run tag @s add capskills.skill.lame_fente_imperiale_1
execute if entity @s[tag=capskills.magie.quick.r1] run tag @s add capskills.skill.magie_canalisation_rapide_1
execute if entity @s[tag=capskills.magie.concentration.r1] run tag @s add capskills.skill.magie_concentration_coeur_1
execute if entity @s[tag=capskills.magie.contact.r1] run tag @s add capskills.skill.magie_contact_recul_1
execute if entity @s[tag=capskills.magie.r2] run tag @s add capskills.skill.magie_decharge_1
execute if entity @s[tag=capskills.magie.dps.r1] run tag @s add capskills.skill.magie_decharge_1
execute if entity @s[tag=capskills.magie.r3] run tag @s add capskills.skill.magie_decharge_2
execute if entity @s[tag=capskills.magie.dps.r2] run tag @s add capskills.skill.magie_decharge_2
execute if entity @s[tag=capskills.magie.control.r1] run tag @s add capskills.skill.magie_entrave_1
execute if entity @s[tag=capskills.magie.control.r2] run tag @s add capskills.skill.magie_entrave_2
execute if entity @s[tag=capskills.magie.r1] run tag @s add capskills.skill.magie_focus_coeur_1
execute if entity @s[tag=capskills.magie.dot.r1] run tag @s add capskills.skill.magie_marque_corrosive_1
execute if entity @s[tag=capskills.magie.resonance.r1] run tag @s add capskills.skill.magie_resonance_1
execute if entity @s[tag=capskills.magie.resonance.r2] run tag @s add capskills.skill.magie_resonance_2
execute if entity @s[tag=capskills.magie.support.r1] run tag @s add capskills.skill.magie_soutien_coeur_1
execute if entity @s[tag=capskills.magie.support.r2] run tag @s add capskills.skill.magie_soutien_coeur_2
execute if entity @s[tag=capskills.magie.surcharge.r1] run tag @s add capskills.skill.magie_surcharge_controlee_1
execute if entity @s[tag=capskills.ombre.effacement.r1] run tag @s add capskills.skill.ombre_effacement_1
execute if entity @s[tag=capskills.ombre.bribe.r1] run tag @s add capskills.skill.ombre_infiltration_1
execute if entity @s[tag=capskills.ombre.bribe.r2] run tag @s add capskills.skill.ombre_infiltration_2
execute if entity @s[tag=capskills.ombre.shadowstep.r1] run tag @s add capskills.skill.ombre_pas_dephase_1
execute if entity @s[tag=capskills.ombre.shadowstep.r2] run tag @s add capskills.skill.ombre_pas_dephase_2
execute if entity @s[tag=capskills.ombre.interrupt.r1] run tag @s add capskills.skill.ombre_interrupt_dephase_1
execute if entity @s[tag=capskills.ombre.pas_silencieux.r1] run tag @s add capskills.skill.ombre_pas_silencieux_1
execute if entity @s[tag=capskills.ombre.r1] run tag @s add capskills.skill.ombre_voie_ombres_1
execute if entity @s[tag=capskills.mineur.r1] run tag @s add capskills.skill.profondeurs_mineur_1
execute if entity @s[tag=capskills.mineur.r2] run tag @s add capskills.skill.profondeurs_mineur_2
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] run tag @s add capskills.skill.profondeurs_prospection_1
execute if entity @s[tag=capskills.mineur.bonus_ores.r2] run tag @s add capskills.skill.profondeurs_prospection_2
execute if entity @s[tag=capskills.rempart.ancrage.r1] run tag @s add capskills.skill.rempart_ancrage_front_1
execute if entity @s[tag=capskills.rempart.r2] run tag @s add capskills.skill.rempart_bastion_1
execute if entity @s[tag=capskills.rempart.bastion.r1] run tag @s add capskills.skill.rempart_bastion_1
execute if entity @s[tag=capskills.rempart.r3] run tag @s add capskills.skill.rempart_bastion_2
execute if entity @s[tag=capskills.rempart.bastion.r2] run tag @s add capskills.skill.rempart_bastion_2
execute if entity @s[tag=capskills.rempart.briseligne.r1] run tag @s add capskills.skill.rempart_brise_ligne_1
execute if entity @s[tag=capskills.rempart.briseligne.r2] run tag @s add capskills.skill.rempart_brise_ligne_2
execute if entity @s[tag=capskills.rempart.shield_durability.r1] run tag @s add capskills.skill.rempart_bouclier_imperial_1
execute if entity @s[tag=capskills.rempart.shield_durability.r2] run tag @s add capskills.skill.rempart_bouclier_imperial_2
execute if entity @s[tag=capskills.rempart.root.r1] run tag @s add capskills.skill.rempart_root_1
execute if entity @s[tag=capskills.rempart.egide.zone.r1] run tag @s add capskills.skill.rempart_egide_2
execute if entity @s[tag=capskills.rempart.egide.r2] run tag @s add capskills.skill.rempart_egide_2
execute if entity @s[tag=capskills.rempart.r1] run tag @s add capskills.skill.rempart_imperial_1
execute if entity @s[tag=capskills.rempart.posture.r1] run tag @s add capskills.skill.rempart_posture_garde_1
execute if entity @s[tag=capskills.rempart.taunt_mmo.r1] run tag @s add capskills.skill.rempart_provocation_imperiale_1
execute if entity @s[tag=capskills.secours.gestes.r1] run tag @s add capskills.skill.secours_gestes_urgence_1
execute if entity @s[tag=capskills.secours.r1] run tag @s add capskills.skill.secours_imperial_1
execute if entity @s[tag=capskills.secours.r2] run tag @s add capskills.skill.secours_medecin_campagne_1
execute if entity @s[tag=capskills.secours.medic.r1] run tag @s add capskills.skill.secours_medecin_campagne_1
execute if entity @s[tag=capskills.secours.r3] run tag @s add capskills.skill.secours_medecin_campagne_2
execute if entity @s[tag=capskills.secours.medic.r2] run tag @s add capskills.skill.secours_medecin_campagne_2
execute if entity @s[tag=capskills.secours.triage.r1] run tag @s add capskills.skill.secours_triage_campagne_1
execute if entity @s[tag=capskills.support.coordination.r1] run tag @s add capskills.skill.support_coordination_1
execute if entity @s[tag=capskills.support.purification.r1] run tag @s add capskills.skill.support_purification_1
execute if entity @s[tag=capskills.support.r1] run tag @s add capskills.skill.support_tactique_1
execute if entity @s[tag=capskills.support.r2] run tag @s add capskills.skill.support_tactique_2
execute if entity @s[tag=capskills.support.r3] run tag @s add capskills.skill.support_tactique_3
execute if entity @s[tag=capskills.trait.main_sure.r1] run tag @s add capskills.skill.trait_main_sure_1
execute if entity @s[tag=capskills.trait.rafale.r1] run tag @s add capskills.skill.trait_rafale_reglementaire_1
execute if entity @s[tag=capskills.trait.rafale.r2] run tag @s add capskills.skill.trait_rafale_reglementaire_2
# 0.8.30-rc4 : migration marqueurs nouveaux perks et Entrave Altération.
execute if entity @s[tag=capskills.alteration.entrave.r1] run tag @s add capskills.skill.magie_entrave_1
execute if entity @s[tag=capskills.alteration.entrave.r2] run tag @s add capskills.skill.magie_entrave_2
execute if entity @s[tag=capskills.ombre.cache.r1] run tag @s add capskills.skill.ombre_cache_1
execute if entity @s[tag=capskills.ombre.cache.r2] run tag @s add capskills.skill.ombre_cache_2
execute if entity @s[tag=capskills.enclume.repair.r1] run tag @s add capskills.skill.artisanat_reparateur_enclume_1
# 0.9.14 : synchronisation arbalète ajoutée après coup, pour éviter les profils avec marqueur UI mais sans tag fonctionnel.
execute if entity @s[tag=capskills.arbalete.antiarmor.r1] run tag @s add capskills.skill.trait_arbalete_antarmure_1
execute if entity @s[tag=capskills.arbalete.salve.r1] run tag @s add capskills.skill.trait_salve_preparee_1
execute if entity @s[tag=capskills.arbalete.stabilisation.r1] run tag @s add capskills.skill.trait_arbalete_stabilisation_1
execute if entity @s[tag=capskills.arbalete.percearmure.r1] run tag @s add capskills.skill.trait_arbalete_percearmure_1
execute if entity @s[tag=capskills.arbalete.carreau_arret.r1] run tag @s add capskills.skill.trait_arbalete_carreau_arret_1
execute if entity @s[tag=capskills.arbalete.vulnerabilite.r1] run tag @s add capskills.skill.trait_arbalete_marque_vulnerabilite_1
execute if entity @s[tag=capskills.skill.trait_arbalete_antarmure_1] run tag @s add capskills.arbalete.antiarmor.r1
execute if entity @s[tag=capskills.skill.trait_salve_preparee_1] run tag @s add capskills.arbalete.salve.r1
execute if entity @s[tag=capskills.skill.trait_arbalete_stabilisation_1] run tag @s add capskills.arbalete.stabilisation.r1
execute if entity @s[tag=capskills.skill.trait_arbalete_percearmure_1] run tag @s add capskills.arbalete.percearmure.r1
execute if entity @s[tag=capskills.skill.trait_arbalete_carreau_arret_1] run tag @s add capskills.arbalete.carreau_arret.r1

execute if entity @s[tag=capskills.skill.trait_arbalete_marque_vulnerabilite_1] run tag @s add capskills.arbalete.vulnerabilite.r1
execute if entity @s[tag=capskills.skill.ombre_interrupt_dephase_1] run tag @s add capskills.ombre.interrupt.r1

# 0.9.39 : Chaman / Totem de Guérison.
execute if entity @s[tag=capskills.chaman.totem.guerison.r1] run tag @s add capskills.skill.chaman_totem_guerison_1
execute if entity @s[tag=capskills.skill.chaman_totem_guerison_1] run tag @s add capskills.chaman.totem.guerison.r1

# 0.9.40 : Chaman / Totem de Givre.
execute if entity @s[tag=capskills.chaman.totem.givre.r1] run tag @s add capskills.skill.chaman_totem_givre_1
execute if entity @s[tag=capskills.skill.chaman_totem_givre_1] run tag @s add capskills.chaman.totem.givre.r1

# 0.9.57 : marqueurs actifs des sorts réintroduits.
execute if entity @s[tag=capskills.lame.tourbillon.r1] run tag @s add capskills.skill.lame_tourbillon_imperial_1
execute if entity @s[tag=capskills.skill.lame_tourbillon_imperial_1] run tag @s add capskills.lame.tourbillon.r1
execute if entity @s[tag=capskills.trait.fleche_entrave.r1] run tag @s add capskills.skill.trait_fleche_entrave_1
execute if entity @s[tag=capskills.skill.trait_fleche_entrave_1] run tag @s add capskills.trait.fleche_entrave.r1
execute if entity @s[tag=capskills.arbalete.rupture.r1] run tag @s add capskills.skill.trait_arbalete_carreau_rupture_1
execute if entity @s[tag=capskills.skill.trait_arbalete_carreau_rupture_1] run tag @s add capskills.arbalete.rupture.r1
execute if entity @s[tag=capskills.ombre.frappe_sortie.r1] run tag @s add capskills.skill.ombre_frappe_sortie_1
execute if entity @s[tag=capskills.skill.ombre_frappe_sortie_1] run tag @s add capskills.ombre.frappe_sortie.r1
execute if entity @s[tag=capskills.rempart.pull.r1] run tag @s add capskills.skill.rempart_poigne_rempart_1
execute if entity @s[tag=capskills.skill.rempart_poigne_rempart_1] run tag @s add capskills.rempart.pull.r1

# 0.9.150 — branche armes à feu.
execute if entity @s[tag=capskills.firearm.mastery.r1] run tag @s add capskills.skill.trait_maitrise_armes_feu_1
execute if entity @s[tag=capskills.skill.trait_maitrise_armes_feu_1] run tag @s add capskills.firearm.mastery.r1
# 0.9.152 — armes à feu II : réparation bidirectionnelle marqueur UI / tag fonctionnel.
execute if entity @s[tag=capskills.firearm.mastery.r2] run tag @s add capskills.skill.trait_maitrise_armes_feu_2
execute if entity @s[tag=capskills.skill.trait_maitrise_armes_feu_2] run tag @s add capskills.firearm.mastery.r2
