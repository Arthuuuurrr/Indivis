# CapSkills 0.9.13 — Salve préparée : canalisation longue fiable + projectile accéléré.
# @s = arbalétrier. Se charge pendant 100 ticks (~5 s), puis arme le prochain carreau.
execute if entity @s[tag=capskills.arbalete.salve.channeling] run scoreboard players add @s CAPSK_CROSS_CHARGE 1

# Télégraphe économe : pulses espacés, aucun nuage persistant à chaque tick.
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 1 run title @s actionbar {"text":"Salve préparée : verrouillage...","color":"gold"}
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 1 at @s run function capskills:visual/arbalete_charge_small_self
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 20 at @s run function capskills:visual/arbalete_charge_small_self
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 40 at @s run function capskills:visual/arbalete_charge_medium_self
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 60 at @s run function capskills:visual/arbalete_charge_medium_self
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 80 at @s run function capskills:visual/arbalete_charge_large_self
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 95 at @s run function capskills:visual/arbalete_charge_large_self

# Actionbar progressive.
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 1..35 run title @s actionbar {"text":"Salve préparée : verrouillage du mécanisme...","color":"gold"}
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 36..74 run title @s actionbar {"text":"Salve préparée : tension mécanique...","color":"yellow"}
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 75..99 run title @s actionbar {"text":"Salve préparée : pression maximale...","color":"red"}

# Sons de progression, audibles mais espacés.
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 1 at @s run playsound minecraft:item.crossbow.loading_start player @a[distance=..24] ~ ~ ~ 0.45 0.70 0
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 40 at @s run playsound minecraft:item.crossbow.loading_middle player @a[distance=..28] ~ ~ ~ 0.55 0.80 0
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 80 at @s run playsound minecraft:block.chain.hit player @a[distance=..30] ~ ~ ~ 0.45 0.65 0

# Charge complète : arme le prochain impact, cooldown dédié plus lourd, puis stoppe la canalisation.
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run tag @s add capskills.arbalete.salve.ready
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run scoreboard players set @s CAPSK_CROSS_READY 1
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run scoreboard players set @s CAPSK_CROSS_USED 0
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run scoreboard players set @s CAPSK_CROSS_CD 30
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. at @s run function capskills:visual/activation/arbalete_salve_ready_self
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run title @s actionbar {"text":"Salve préparée armée — un seul carreau accéléré","color":"green"}
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run scoreboard players set @s CAPSK_CROSS_CHARGE 100
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run tag @s remove capskills.arbalete.salve.forced_channel
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run tag @s remove capskills.arbalete.salve.channeling
