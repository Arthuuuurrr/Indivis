# CapSkills 0.9.25 — Onde de recul validée par bridge d'attaque PlayerEntity.
# @s = lanceur, cible = entité taggée capskills.mod_hit_target par le hook d’attaque du jar.
# Le recul est volontairement laissé au jar via addVelocity/takeKnockback : aucun fallback tp.

# Marqueurs lus immédiatement par le jar après l'appel de cette fonction.
tag @s add capskills.mod_knockback_pending
execute as @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run tag @s add capskills.mod_knockback_target

execute at @s run function capskills:visual/activation/magie_knockback_self
execute at @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run particle minecraft:sonic_boom ~ ~1.0 ~ 0.30 0.40 0.30 0.02 10 force @a[distance=..28]
execute at @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run particle minecraft:end_rod ~ ~1.0 ~ 0.85 0.70 0.85 0.06 60 force @a[distance=..28]
execute at @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] run particle minecraft:gust ~ ~1.0 ~ 0.40 0.30 0.40 0.02 12 force @a[distance=..28]

# Dégâts/contrôle existants.
damage @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] 1 minecraft:generic by @s
effect give @e[tag=capskills.mod_hit_target,limit=1,sort=nearest] minecraft:slowness 2 0 true

scoreboard players set @s CAPSK_MAGE_CONTACT_CD 12
tag @s add capskills.mage.contact.just_triggered
execute at @s run playsound minecraft:entity.warden.sonic_boom player @a[distance=..28] ~ ~ ~ 0.55 1.70 0
title @s actionbar {"text":"Onde de recul libérée. Recharge : 12 s.","color":"light_purple"}
