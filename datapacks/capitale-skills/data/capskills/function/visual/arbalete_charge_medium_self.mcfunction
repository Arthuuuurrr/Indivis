# Salve préparée — tension mécanique lisible.
execute at @s run particle minecraft:electric_spark ~ ~0.35 ~ 0.90 0.08 0.90 0.01 14 force @a[distance=..36]
execute at @s run particle minecraft:enchanted_hit ~ ~0.75 ~ 0.45 0.20 0.45 0.02 10 force @a[distance=..36]
execute at @s anchored eyes rotated as @s positioned ^0.55 ^-0.25 ^0.65 run particle minecraft:crit ~ ~ ~ 0.05 0.05 0.05 0.01 5 force @a[distance=..24]
execute at @s anchored eyes rotated as @s positioned ^-0.55 ^-0.25 ^0.65 run particle minecraft:crit ~ ~ ~ 0.05 0.05 0.05 0.01 5 force @a[distance=..24]
