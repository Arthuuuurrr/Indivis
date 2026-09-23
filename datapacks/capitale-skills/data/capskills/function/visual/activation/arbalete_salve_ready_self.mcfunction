# CapSkills 0.9.13 — Salve préparée armée : signal lourd, distinct, non intrusif.
execute at @s run particle minecraft:sonic_boom ~ ~0.45 ~ 0.12 0.06 0.12 0.00 1 force @a[distance=..48]
execute at @s run particle minecraft:end_rod ~ ~0.35 ~ 1.15 0.12 1.15 0.01 24 force @a[distance=..48]
execute at @s run particle minecraft:electric_spark ~ ~0.75 ~ 0.80 0.22 0.80 0.03 24 force @a[distance=..48]
execute at @s run playsound minecraft:item.crossbow.loading_end player @a[distance=..36] ~ ~ ~ 0.80 0.55 0
execute at @s run playsound minecraft:block.anvil.use player @a[distance=..32] ~ ~ ~ 0.45 1.35 0
