# CapSkills 0.9.19 — fallback de tir Salve préparée.
# @s = arbalétrier. Déclenché par minecraft.used:minecraft.crossbow.
# Le fallback direct applique les effets par raycast, indépendamment du projectile Java.
scoreboard players set @s CAPSK_CROSS_USED 0
scoreboard players set @s CAPSK_CROSS_SHOT_T 8
tag @s add capskills.arbalete.salve.pending_clear
tag @s add capskills.arbalete.salve.just_shot
function capskills:bridge/combat/crossbow_direct_shot_self
execute at @s run particle minecraft:end_rod ~ ~0.55 ~ 0.70 0.12 0.70 0.02 12 force @a[distance=..36]
execute at @s run playsound minecraft:item.crossbow.shoot player @a[distance=..36] ~ ~ ~ 0.55 0.55 0
execute if score @s CAPSK_CROSS_DIRECT_HIT matches 1.. run title @s actionbar {"text":"Salve libérée — cible marquée","color":"yellow"}
execute if score @s CAPSK_CROSS_DIRECT_HIT matches 0 run title @s actionbar {"text":"Salve libérée — aucun impact direct détecté","color":"yellow"}
