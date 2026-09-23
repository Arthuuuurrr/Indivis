# 0.9.42 — amélioration passive : les totems du Chaman gênent toutes les cibles proches.
# Le ciblage allié/ennemi sera affiné plus tard par un autre mod ; en l'état, les joueurs sont volontairement touchés.
effect give @a[distance=..7] minecraft:weakness 3 0 true
effect give @a[distance=..7] minecraft:glowing 3 0 true
effect give @e[type=minecraft:zombie,distance=..7] minecraft:weakness 3 0 true
effect give @e[type=minecraft:zombie,distance=..7] minecraft:glowing 3 0 true
effect give @e[type=minecraft:skeleton,distance=..7] minecraft:weakness 3 0 true
effect give @e[type=minecraft:skeleton,distance=..7] minecraft:glowing 3 0 true
effect give @e[type=minecraft:spider,distance=..7] minecraft:weakness 3 0 true
effect give @e[type=minecraft:spider,distance=..7] minecraft:glowing 3 0 true
effect give @e[type=minecraft:creeper,distance=..7] minecraft:weakness 3 0 true
effect give @e[type=minecraft:creeper,distance=..7] minecraft:glowing 3 0 true
effect give @e[type=minecraft:husk,distance=..7] minecraft:weakness 3 0 true
effect give @e[type=minecraft:husk,distance=..7] minecraft:glowing 3 0 true
effect give @e[type=minecraft:drowned,distance=..7] minecraft:weakness 3 0 true
effect give @e[type=minecraft:drowned,distance=..7] minecraft:glowing 3 0 true
effect give @e[type=minecraft:stray,distance=..7] minecraft:weakness 3 0 true
effect give @e[type=minecraft:stray,distance=..7] minecraft:glowing 3 0 true
effect give @e[type=minecraft:pillager,distance=..7] minecraft:weakness 3 0 true
effect give @e[type=minecraft:pillager,distance=..7] minecraft:glowing 3 0 true
effect give @e[type=minecraft:vindicator,distance=..7] minecraft:weakness 3 0 true
effect give @e[type=minecraft:vindicator,distance=..7] minecraft:glowing 3 0 true
effect give @e[type=minecraft:witch,distance=..7] minecraft:weakness 3 0 true
effect give @e[type=minecraft:witch,distance=..7] minecraft:glowing 3 0 true
effect give @e[type=minecraft:enderman,distance=..7] minecraft:weakness 3 0 true
effect give @e[type=minecraft:enderman,distance=..7] minecraft:glowing 3 0 true
particle minecraft:electric_spark ~ ~0.9 ~ 1.0 0.35 1.0 0.03 24 force @a[distance=..28]
playsound minecraft:block.copper_bulb.turn_on player @a[distance=..16] ~ ~ ~ 0.25 1.70 0
