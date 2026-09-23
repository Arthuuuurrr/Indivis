# CapSkills 0.9.13 — interruption de Salve préparée.
execute if entity @s[tag=capskills.arbalete.salve.channeling] unless score @s CAPSK_CROSS_CHARGE matches 100.. run title @s actionbar {"text":"Salve préparée interrompue.","color":"gray"}
execute if entity @s[tag=capskills.arbalete.salve.channeling] at @s run particle minecraft:smoke ~ ~0.45 ~ 0.55 0.10 0.55 0.01 10 force @a[distance=..24]
execute if entity @s[tag=capskills.arbalete.salve.channeling] at @s run playsound minecraft:item.crossbow.quick_charge_1 player @s ~ ~ ~ 0.35 0.55 0
tag @s remove capskills.arbalete.salve.channeling
tag @s remove capskills.arbalete.salve.usage_tick
tag @s remove capskills.arbalete.salve.forced_channel
scoreboard players set @s CAPSK_CROSS_CHARGE 0
scoreboard players set @s CAPSK_CROSS_READY 0
