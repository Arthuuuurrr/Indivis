scoreboard players set #clock CAPSK_CLOCK 0
# 0.8.5d : initialise les scores joueurs avant les tests/decrementation de cooldown.
scoreboard players remove @a[scores={CAPSK_HEAL_CD=1..}] CAPSK_HEAL_CD 1
scoreboard players remove @a[scores={CAPSK_ZONE_CD=1..}] CAPSK_ZONE_CD 1
scoreboard players remove @a[scores={CAPSK_AEGIS_CD=1..}] CAPSK_AEGIS_CD 1
scoreboard players remove @a[scores={CAPSK_REMP_SELF_CD=1..}] CAPSK_REMP_SELF_CD 1
scoreboard players remove @a[scores={CAPSK_REMP_TARGET_CD=1..}] CAPSK_REMP_TARGET_CD 1
scoreboard players remove @a[scores={CAPSK_REMP_ZONE_CD=1..}] CAPSK_REMP_ZONE_CD 1
scoreboard players remove @a[scores={CAPSK_TAUNT_CD=1..}] CAPSK_TAUNT_CD 1
scoreboard players remove @a[scores={CAPSK_REMP_ROOT_CD=1..}] CAPSK_REMP_ROOT_CD 1
scoreboard players remove @a[scores={CAPSK_ALCH_CD=1..}] CAPSK_ALCH_CD 1
scoreboard players remove @a[scores={CAPSK_SELF_CD=1..}] CAPSK_SELF_CD 1
scoreboard players remove @a[scores={CAPSK_MAGIE_CD=1..}] CAPSK_MAGIE_CD 1
scoreboard players remove @a[scores={CAP_PVP_TIMER=1..}] CAP_PVP_TIMER 1
scoreboard players set @a[scores={CAP_PVP_TIMER=0}] CAP_PVP 0
# 0.9.152 — synchronisation événementielle : seulement nouveaux joueurs ou arbre modifié.
execute as @a unless score @s CAPSK_RUNTIME_VER matches 152 run function capskills:integration/sync_all_self
execute as @a[scores={CAPSK_SYNC_DIRTY=1..}] run function capskills:integration/sync_all_self

scoreboard players remove @a[scores={CAPSK_RAFALE_CD=1..}] CAPSK_RAFALE_CD 1
scoreboard players remove @a[scores={CAPSK_LAME_CD=1..}] CAPSK_LAME_CD 1
scoreboard players remove @a[scores={CAPSK_OMBRESTEP_CD=1..}] CAPSK_OMBRESTEP_CD 1
scoreboard players remove @a[scores={CAPSK_OMBRE_INT_CD=1..}] CAPSK_OMBRE_INT_CD 1
scoreboard players remove @a[scores={CAPSK_ALTER_TARGET_CD=1..}] CAPSK_ALTER_TARGET_CD 1
scoreboard players remove @a[scores={CAPSK_ALTER_LEV_CD=1..}] CAPSK_ALTER_LEV_CD 1
scoreboard players remove @a[scores={CAPSK_MAG_DOT_CD=1..}] CAPSK_MAG_DOT_CD 1
scoreboard players remove @a[scores={CAPSK_MAGE_CONTACT_CD=1..}] CAPSK_MAGE_CONTACT_CD 1


# 0.9.0-rc4b : triggers joueurs non-op pour les récupérations quotidiennes.
scoreboard players enable @a CAPSK_ALCH_CLAIM
scoreboard players enable @a CAPSK_SHADOW_CLAIM
execute as @a[scores={CAPSK_ALCH_CLAIM=1..}] run function capskills:reward/alchemy/claim_self
scoreboard players set @a[scores={CAPSK_ALCH_CLAIM=1..}] CAPSK_ALCH_CLAIM 0
execute as @a[scores={CAPSK_SHADOW_CLAIM=1..}] run function capskills:reward/shadow/claim_self
scoreboard players set @a[scores={CAPSK_SHADOW_CLAIM=1..}] CAPSK_SHADOW_CLAIM 0

# 0.8.25 / 0.9.0-rc4b : bouton de connexion conditionnel et prospection vanilla.

# 0.8.29 : artisanat civil avancé.
# 0.8.30-rc4 : cooldown remboursement enclume.
scoreboard players remove @a[scores={CAPSK_ANVIL_CD=1..}] CAPSK_ANVIL_CD 1
# 0.9.3 : cooldown futur arbalète.
scoreboard players remove @a[scores={CAPSK_CROSS_CD=1..}] CAPSK_CROSS_CD 1
scoreboard players remove @a[scores={CAPSK_ARBA_MARK_CD=1..}] CAPSK_ARBA_MARK_CD 1
scoreboard players remove @a[scores={CAPSK_CHAMAN_TOTEM_CD=1..}] CAPSK_CHAMAN_TOTEM_CD 1
scoreboard players remove @a[tag=capskills.arbalete.vulnerabilite.loaded,scores={CAPSK_ARBA_MARK_T=1..}] CAPSK_ARBA_MARK_T 1
execute as @a[tag=capskills.arbalete.vulnerabilite.loaded,scores={CAPSK_ARBA_MARK_T=0}] at @s run function capskills:skill/arbalete/marque_vulnerabilite_expire_self
scoreboard players remove @a[scores={CAPSK_EXEC_CD=1..}] CAPSK_EXEC_CD 1
scoreboard players remove @a[scores={CAPSK_DUAL_PASS_CD=1..}] CAPSK_DUAL_PASS_CD 1

# 0.9.57 : cooldowns/états des sorts réintroduits.
scoreboard players remove @a[scores={CAPSK_LAME_TOURB_CD=1..}] CAPSK_LAME_TOURB_CD 1
scoreboard players remove @a[scores={CAPSK_BOW_ENTRAVE_CD=1..}] CAPSK_BOW_ENTRAVE_CD 1
scoreboard players remove @a[tag=capskills.trait.fleche_entrave.loaded,scores={CAPSK_BOW_ENTRAVE_T=1..}] CAPSK_BOW_ENTRAVE_T 1
execute as @a[tag=capskills.trait.fleche_entrave.loaded,scores={CAPSK_BOW_ENTRAVE_T=0}] at @s run function capskills:skill/trait/fleche_entrave_expire_self
scoreboard players remove @a[tag=capskills.ombre.sortie.ready,scores={CAPSK_OMBRE_SORTIE_T=1..}] CAPSK_OMBRE_SORTIE_T 1
execute as @a[tag=capskills.ombre.sortie.ready,scores={CAPSK_OMBRE_SORTIE_T=0}] at @s run function capskills:skill/ombre/frappe_sortie_expire_self

# 0.9.50 : cooldown Poigne du Rempart.
scoreboard players remove @a[scores={CAPSK_REMP_PULL_CD=1..}] CAPSK_REMP_PULL_CD 1

# 0.9.65 EXP : expiration du Tourbillon armé.
scoreboard players remove @a[tag=capskills.lame.tourbillon.armed,scores={CAPSK_LAME_TOURB_ARM_T=1..}] CAPSK_LAME_TOURB_ARM_T 1
execute as @a[tag=capskills.lame.tourbillon.armed,scores={CAPSK_LAME_TOURB_ARM_T=0}] at @s run function capskills:skill/lame/tourbillon_expire_self

# CapSkills 0.9.67 EXP — sync automatique des familles d’armes désactivé.
# Les skills critiques doivent tester directement l’item tenu au moment de l’activation/hit.
# Utiliser /function capskills:weapon/sync_family_self uniquement pour diagnostic manuel.
scoreboard players remove @a[scores={CAPSK_LANCE_CD=1..}] CAPSK_LANCE_CD 1

# 0.9.150 — feedback Salve limité à une fois par seconde au lieu de chaque tick.
execute as @a[tag=capskills.arbalete.salve.ready,nbt={SelectedItem:{id:"minecraft:crossbow"}}] at @s if predicate capskills:player/is_sneaking unless entity @s[tag=capskills.arbalete.salve.pending_clear] run title @s actionbar {"text":"Salve préparée armée — prochain carreau accéléré","color":"green"}

# Audit complet de sécurité toutes les 30 secondes seulement.
scoreboard players add #audit CAPSK_CLOCK 1
execute if score #audit CAPSK_CLOCK matches 30.. run function capskills:integration/sync_audit_self

# Tâches non critiques toutes les cinq secondes.
scoreboard players add #slow CAPSK_CLOCK 1
execute if score #slow CAPSK_CLOCK matches 5.. run function capskills:tick_5s


# 0.10.14 — maîtrise de l’arc : attribut appliqué uniquement avec un arc réellement tenu.
execute as @a[tag=capskills.maitrise_arc.r1] run function capskills:skill/maitrise/arc_sync_self
