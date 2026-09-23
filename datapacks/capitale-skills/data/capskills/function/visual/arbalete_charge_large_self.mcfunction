# Salve préparée — seuil maximal, périphérique pour ne pas masquer la visée.
execute at @s run particle minecraft:end_rod ~ ~0.45 ~ 1.05 0.10 1.05 0.01 18 force @a[distance=..40]
execute at @s run particle minecraft:electric_spark ~ ~0.75 ~ 0.70 0.20 0.70 0.02 20 force @a[distance=..40]
execute at @s anchored eyes rotated as @s positioned ^0.70 ^-0.30 ^0.80 run particle minecraft:enchanted_hit ~ ~ ~ 0.05 0.05 0.05 0.02 8 force @a[distance=..28]
execute at @s anchored eyes rotated as @s positioned ^-0.70 ^-0.30 ^0.80 run particle minecraft:enchanted_hit ~ ~ ~ 0.05 0.05 0.05 0.02 8 force @a[distance=..28]
