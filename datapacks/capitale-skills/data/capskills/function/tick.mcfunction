# CapSkills BETA 0.9.0-rc1 — tick minimal + Rafale espacée.
# Les vrais items du mod Fabric appellent directement les bridges :
# - capskills:bridge/item/secours_use_self
# - capskills:bridge/item/rempart_use_self
# - capskills:bridge/item/alchimie_use_self
# - capskills:bridge/item/magie_use_self
# L'écoute des anciens items vanilla custom a été désactivée pour éviter une couche obsolète permanente.

# 0.9.0-rc1 : traite les impacts espacés de Rafale à chaque tick.
execute as @a[tag=capskills.trait.rafale.bursting] at @s run function capskills:skill/trait/burst_tick_self
execute as @a[tag=capskills.trait.rafale.channeling] at @s run function capskills:skill/trait/channel_tick_self
# 0.9.69 EXP : Tourbillon canalisé, puis actif uniquement pendant la fenêtre du sort.
execute as @a[tag=capskills.lame.tourbillon.channeling] at @s run function capskills:skill/lame/tourbillon_channel_tick_self
execute as @a[tag=capskills.lame.tourbillon.spinning] at @s run function capskills:skill/lame/tourbillon_tick_self
# 0.9.9 : visuel Rafale unique et économe.
# Le vieux visual_channel_tick est désactivé ici pour éviter le double rendu / spam particules.
# Déclenchement : Rafale débloquée + arc sélectionné + accroupi + cooldown prêt.
execute as @a[tag=capskills.trait.rafale.r1,nbt={SelectedItem:{id:"minecraft:bow"}},scores={CAPSK_RAFALE_CD=0}] at @s if predicate capskills:player/is_sneaking run function capskills:skill/trait/force_bow_charge_visual_self
execute as @a[tag=capskills.trait.rafale.force_visual] at @s unless predicate capskills:player/is_sneaking run function capskills:skill/trait/force_bow_charge_clear_self
execute as @a[tag=capskills.trait.rafale.force_visual] at @s unless entity @s[nbt={SelectedItem:{id:"minecraft:bow"}}] run function capskills:skill/trait/force_bow_charge_clear_self
execute as @a[tag=capskills.trait.rafale.force_visual,scores={CAPSK_RAFALE_CD=1..}] at @s run function capskills:skill/trait/force_bow_charge_clear_self
# 0.9.13/0.9.14 : Salve préparée — fallback datapack fiable, comme Rafale.
# Déclenchement : Salve débloquée + arbalète sélectionnée + accroupi + cooldown prêt.
# 0.9.14 : Salve préparée — déclenchement tolérant si le tag fonctionnel n’a pas été posé mais que le marqueur de skill existe.
execute as @a[tag=capskills.arbalete.salve.r1,nbt={SelectedItem:{id:"minecraft:crossbow"}},scores={CAPSK_CROSS_CD=0}] at @s if predicate capskills:player/is_sneaking unless entity @s[tag=capskills.arbalete.salve.channeling] unless entity @s[tag=capskills.arbalete.salve.ready] run function capskills:skill/arbalete/salve_force_start_self
execute as @a[tag=capskills.skill.trait_salve_preparee_1,nbt={SelectedItem:{id:"minecraft:crossbow"}},scores={CAPSK_CROSS_CD=0}] at @s if predicate capskills:player/is_sneaking unless entity @s[tag=capskills.arbalete.salve.channeling] unless entity @s[tag=capskills.arbalete.salve.ready] run function capskills:skill/arbalete/salve_force_start_self
execute as @a[tag=capskills.arbalete.salve.channeling] at @s unless predicate capskills:player/is_sneaking run function capskills:skill/arbalete/salve_cancel_self
execute as @a[tag=capskills.arbalete.salve.channeling] at @s unless entity @s[nbt={SelectedItem:{id:"minecraft:crossbow"}}] run function capskills:skill/arbalete/salve_cancel_self
execute as @a[tag=capskills.arbalete.salve.channeling] at @s run function capskills:skill/arbalete/salve_prepare_tick_self

# 0.9.16 : si Salve est prête et que l’arbalète est tirée, on garde la fenêtre de boost quelques ticks
# pour laisser le hook projectile marquer le carreau, puis on vide l’état même si le carreau rate.
# 0.9.22 CLEANUP : ancien déclencheur minecraft.used:minecraft.crossbow désactivé (ambigu usage/chargement/tir).
# execute as @a[tag=capskills.arbalete.salve.ready,scores={CAPSK_CROSS_USED=1..}] at @s run function capskills:bridge/combat/crossbow_shot_fallback_self
# 0.9.19 : fallback direct pour les perks d’arbalète hors Salve, sans dépendre des tags projectile.
# execute as @a[scores={CAPSK_CROSS_USED=1..}] at @s unless entity @s[tag=capskills.arbalete.salve.ready] run function capskills:bridge/combat/crossbow_direct_shot_self
execute as @a[scores={CAPSK_CROSS_USED=1..}] run scoreboard players set @s CAPSK_CROSS_USED 0
execute as @a[tag=capskills.arbalete.salve.pending_clear,scores={CAPSK_CROSS_SHOT_T=1..}] run scoreboard players remove @s CAPSK_CROSS_SHOT_T 1
execute as @a[tag=capskills.arbalete.salve.pending_clear,scores={CAPSK_CROSS_SHOT_T=0}] at @s run function capskills:bridge/combat/crossbow_salve_expire_self
execute as @a[scores={CAPSK_BOW_USED=1..}] run function capskills:bridge/combat/bow_shot_fallback_self

# 0.9.20 : shot_crossbow advancement = détection réelle du tir d'arbalète.
execute as @a[tag=capskills.crossbow.adv_shot_window,scores={CAPSK_CROSS_ADV_T=1..}] at @s run function capskills:bridge/combat/crossbow_adv_window_tick_self

# 0.9.18 : fallback projectile arbalète — applique les effets si le hook d'impact Java ne passe pas.
execute as @e[tag=capskills.crossbow_projectile] run scoreboard players add @s CAPSK_CROSS_PROJ_AGE 1
# 0.9.22 : fallback datapack de vélocité, indépendant du hook Java.
execute as @e[tag=capskills.crossbow_projectile,tag=!capskills.crossbow_velocity_datapack_boosted,scores={CAPSK_CROSS_PROJ_AGE=0..8}] at @s run function capskills:bridge/combat/crossbow_projectile_velocity_boost_self
execute as @e[tag=capskills.crossbow_projectile,scores={CAPSK_CROSS_PROJ_AGE=3..160}] at @s run function capskills:bridge/combat/crossbow_projectile_fallback_tick
execute as @e[tag=capskills.crossbow_projectile,scores={CAPSK_CROSS_PROJ_AGE=161..}] run function capskills:bridge/combat/crossbow_projectile_cleanup_self


# 0.9.36 : visuel et expiration de la Marque de vulnérabilité.
execute as @e[tag=capskills.arbalete.vulnerable,scores={CAPSK_ARBA_VULN_T=1..}] at @s run function capskills:skill/arbalete/marque_vulnerabilite_visual_tick_as_target
execute as @e[tag=capskills.arbalete.vulnerable,scores={CAPSK_ARBA_VULN_T=..0}] run tag @s remove capskills.arbalete.vulnerable

# 0.9.30 : visuel persistant du Root du Rempart sur la cible.
execute as @e[tag=capskills.rempart.rooted_visual,scores={CAPSK_REMP_ROOT_VIS=1..}] at @s run function capskills:skill/rempart/root_visual_tick_as_target
execute as @e[tag=capskills.rempart.rooted_visual,scores={CAPSK_REMP_ROOT_VIS=..0}] run tag @s remove capskills.rempart.rooted_visual


# 0.9.40 : vrai Fétiche des Ancêtres moddé + totems Chaman. L’ancien carrot_on_a_stick reste accepté en compatibilité.
execute as @a unless score @s CAPSK_UID matches 1.. run function capskills:integration/assign_uid_self
execute as @a[scores={CAPSK_CHAMAN_USE=1..},nbt={SelectedItem:{id:"minecraft:carrot_on_a_stick",components:{"minecraft:custom_data":{capitale_skills:{item:"fetiche_ancetres"}}}}}] at @s run function capskills:bridge/item/chaman_use_self
scoreboard players set @a[scores={CAPSK_CHAMAN_USE=1..}] CAPSK_CHAMAN_USE 0
execute as @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.protection] run scoreboard players remove @s CAPSK_CHAMAN_TOTEM_T 1
execute as @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.protection,scores={CAPSK_CHAMAN_TOTEM_T=1..}] at @s run function capskills:skill/chaman/totem_protection_tick_as_totem
execute as @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.protection,scores={CAPSK_CHAMAN_TOTEM_T=..0}] at @s run function capskills:skill/chaman/totem_protection_remove_as_totem
execute as @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison] run scoreboard players remove @s CAPSK_CHAMAN_TOTEM_T 1
execute as @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison,scores={CAPSK_CHAMAN_TOTEM_T=1..}] at @s run function capskills:skill/chaman/totem_guerison_tick_as_totem
execute as @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison,scores={CAPSK_CHAMAN_TOTEM_T=..0}] at @s run function capskills:skill/chaman/totem_guerison_remove_as_totem

scoreboard players add #clock CAPSK_CLOCK 1
execute if score #clock CAPSK_CLOCK matches 20.. run function capskills:tick_1s

# 0.9.26 : nettoyage du garde anti-double-trigger contact mage.
tag @a[tag=capskills.mage.contact.just_triggered] remove capskills.mage.contact.just_triggered


# 0.9.150 — cadence revolver sans appel de fonction vide pour chaque joueur à chaque tick.
scoreboard players remove @a[scores={CAPREV_COOLDOWN=1..}] CAPREV_COOLDOWN 1
scoreboard players remove @a[scores={CAPREV_RELOAD=1..}] CAPREV_RELOAD 1
execute as @a[tag=capskills.revolver.reloading,scores={CAPREV_RELOAD=0}] at @s run function capskills:revolver/reload_complete_self
