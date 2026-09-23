# CapSkills BETA 0.9.9 — visuel de charge Rafale économe.
# Déclenché par tick.mcfunction : Rafale débloquée + arc sélectionné + sneak + cooldown prêt.
# Pas de particules devant le viseur ; pulses espacés pour limiter le coût réseau/rendu.
tag @s add capskills.trait.rafale.force_visual
scoreboard players add @s CAPSK_RAFALE_FORCE_VIS 1

# Anneaux bas : pulses espacés, pas à chaque tick.
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 1 run function capskills:visual/rafale_force_charge_small_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 6 run function capskills:visual/rafale_force_charge_small_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 12 run function capskills:visual/rafale_force_charge_small_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 18 run function capskills:visual/rafale_force_charge_small_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 24 run function capskills:visual/rafale_force_charge_medium_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 32 run function capskills:visual/rafale_force_charge_medium_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 40 run function capskills:visual/rafale_force_charge_medium_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 48 run function capskills:visual/rafale_force_charge_large_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 56 run function capskills:visual/rafale_force_charge_large_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 64 run function capskills:visual/rafale_force_charge_large_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 72 run function capskills:visual/rafale_force_charge_large_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 84 run function capskills:visual/rafale_force_charge_large_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 96 run function capskills:visual/rafale_force_charge_large_self
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 108 run function capskills:visual/rafale_force_charge_large_self

# Signaux sonores uniques de progression.
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 1 run playsound minecraft:block.beacon.ambient player @s ~ ~ ~ 0.16 1.55 0
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 32 run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.20 1.20 0
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 80 run playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.30 1.85 0

# Actionbar lisible ; peu coûteuse, mais sans particules centrales.
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 1..20 run title @s actionbar {"text":"Rafale : armement...","color":"red"}
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 21..79 run title @s actionbar {"text":"Rafale : tension croissante...","color":"gold"}
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 80.. run title @s actionbar {"text":"Rafale prête — vise puis relâche","color":"green"}

# Boucle de maintien prêt : garde un pulse toutes les ~0,6 s sans empiler les nuages.
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 130.. run scoreboard players set @s CAPSK_RAFALE_FORCE_VIS 100
