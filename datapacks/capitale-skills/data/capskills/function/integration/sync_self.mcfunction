# CapSkills 0.9.150 — synchronisation rapide des rangs.
# L'initialisation complète n'est rejouée que lors d'un changement de version runtime.
execute unless score @s CAPSK_RUNTIME_VER matches 152 run function capskills:integration/init_runtime_self

# Reconstruit les rangs d'aptitude à partir des tags Pufferfish.
scoreboard players set @s CAPSK_SEC_RANK 0
execute if entity @s[tag=capskills.secours.r1] run scoreboard players set @s CAPSK_SEC_RANK 1
execute if entity @s[tag=capskills.secours.r2] run scoreboard players set @s CAPSK_SEC_RANK 2
execute if entity @s[tag=capskills.secours.r3] run scoreboard players set @s CAPSK_SEC_RANK 3

scoreboard players set @s CAPSK_REMP_RANK 0
execute if entity @s[tag=capskills.rempart.r1] run scoreboard players set @s CAPSK_REMP_RANK 1
execute if entity @s[tag=capskills.rempart.r2] run scoreboard players set @s CAPSK_REMP_RANK 2
execute if entity @s[tag=capskills.rempart.r3] run scoreboard players set @s CAPSK_REMP_RANK 3

scoreboard players set @s CAPSK_ALCH_RANK 0
execute if entity @s[tag=capskills.alchimie.r1] run scoreboard players set @s CAPSK_ALCH_RANK 1
execute if entity @s[tag=capskills.alchimie.r2] run scoreboard players set @s CAPSK_ALCH_RANK 2
execute if entity @s[tag=capskills.alchimie.r3] run scoreboard players set @s CAPSK_ALCH_RANK 3


scoreboard players set @s CAPSK_MAG_RANK 0
execute if entity @s[tag=capskills.magie.r1] run scoreboard players set @s CAPSK_MAG_RANK 1
execute if entity @s[tag=capskills.magie.r2] run scoreboard players set @s CAPSK_MAG_RANK 2
execute if entity @s[tag=capskills.magie.r3] run scoreboard players set @s CAPSK_MAG_RANK 3


scoreboard players set @s CAPSK_SELF_UNLOCK 0
execute if entity @s[tag=capskills.secours.selfheal] run scoreboard players set @s CAPSK_SELF_UNLOCK 1
execute if entity @s[tag=capskills.commun.selfheal] run scoreboard players set @s CAPSK_SELF_UNLOCK 1
# 0.8.4 : secours ou magie impliquent la canalisation vitale pour éviter les états joueurs désynchronisés.
execute if score @s CAPSK_SEC_RANK matches 1.. run scoreboard players set @s CAPSK_SELF_UNLOCK 1
execute if score @s CAPSK_MAG_RANK matches 1.. run scoreboard players set @s CAPSK_SELF_UNLOCK 1


# Spécialisations 0.6 — scores reconstruits depuis les tags Pufferfish.
scoreboard players set @s CAPSK_SUPPORT_RANK 0
execute if entity @s[tag=capskills.support.r1] run scoreboard players set @s CAPSK_SUPPORT_RANK 1
execute if entity @s[tag=capskills.support.r2] run scoreboard players set @s CAPSK_SUPPORT_RANK 2
execute if entity @s[tag=capskills.support.r3] run scoreboard players set @s CAPSK_SUPPORT_RANK 3

scoreboard players set @s CAPSK_BASTION_RANK 0
execute if entity @s[tag=capskills.rempart.bastion.r1] run scoreboard players set @s CAPSK_BASTION_RANK 1
execute if entity @s[tag=capskills.rempart.bastion.r2] run scoreboard players set @s CAPSK_BASTION_RANK 2

scoreboard players set @s CAPSK_EGIDE_RANK 0
# 0.9.27 : Égide ciblée retirée ; l'Égide restante est la protection de zone, rang unique.
execute if entity @s[tag=capskills.rempart.egide.zone.r1] run scoreboard players set @s CAPSK_EGIDE_RANK 1
# Compatibilité : ancien tag d'Égide II conservé comme Égide I de zone si déjà présent.
execute if entity @s[tag=capskills.rempart.egide.r2] run scoreboard players set @s CAPSK_EGIDE_RANK 1

scoreboard players set @s CAPSK_BRISE_RANK 0
execute if entity @s[tag=capskills.rempart.briseligne.r1] run scoreboard players set @s CAPSK_BRISE_RANK 1
execute if entity @s[tag=capskills.rempart.briseligne.r2] run scoreboard players set @s CAPSK_BRISE_RANK 2

scoreboard players set @s CAPSK_MAG_DPS_RANK 0
execute if entity @s[tag=capskills.magie.dps.r1] run scoreboard players set @s CAPSK_MAG_DPS_RANK 1
execute if entity @s[tag=capskills.magie.dps.r2] run scoreboard players set @s CAPSK_MAG_DPS_RANK 2

scoreboard players set @s CAPSK_MAG_CTL_RANK 0
execute if entity @s[tag=capskills.magie.control.r1] run scoreboard players set @s CAPSK_MAG_CTL_RANK 1
execute if entity @s[tag=capskills.magie.control.r2] run scoreboard players set @s CAPSK_MAG_CTL_RANK 2

scoreboard players set @s CAPSK_MAG_RES_RANK 0
execute if entity @s[tag=capskills.magie.resonance.r1] run scoreboard players set @s CAPSK_MAG_RES_RANK 1
execute if entity @s[tag=capskills.magie.resonance.r2] run scoreboard players set @s CAPSK_MAG_RES_RANK 2

scoreboard players set @s CAPSK_MAG_SUP_RANK 0
execute if entity @s[tag=capskills.magie.support.r1] run scoreboard players set @s CAPSK_MAG_SUP_RANK 1
execute if entity @s[tag=capskills.magie.support.r2] run scoreboard players set @s CAPSK_MAG_SUP_RANK 2


# 0.8.30-rc4 : rang Entrave désormais côté Altération.
scoreboard players set @s CAPSK_ALTER_CTL_RANK 0
execute if entity @s[tag=capskills.alteration.entrave.r1] run scoreboard players set @s CAPSK_ALTER_CTL_RANK 1
execute if entity @s[tag=capskills.alteration.entrave.r2] run scoreboard players set @s CAPSK_ALTER_CTL_RANK 2

# 0.9.57 — réparation tags depuis les marqueurs UI Puffish.
execute if entity @s[tag=capskills.skill.lame_tourbillon_imperial_1] run tag @s add capskills.lame.tourbillon.r1
execute if entity @s[tag=capskills.skill.trait_fleche_entrave_1] run tag @s add capskills.trait.fleche_entrave.r1
execute if entity @s[tag=capskills.skill.trait_arbalete_carreau_rupture_1] run tag @s add capskills.arbalete.rupture.r1
execute if entity @s[tag=capskills.skill.ombre_frappe_sortie_1] run tag @s add capskills.ombre.frappe_sortie.r1
# 0.9.55 — réparation Poigne du Rempart / cooldown non initialisé.
execute if entity @s[tag=capskills.skill.rempart_poigne_rempart_1] run tag @s add capskills.rempart.pull.r1
