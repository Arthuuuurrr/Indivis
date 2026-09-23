# 0.9.42 — amélioration passive : les totems du Chaman ralentissent toutes les cibles proches.
# Le ciblage allié/ennemi sera affiné plus tard par un autre mod ; en l'état, les joueurs sont volontairement touchés.
effect give @a[distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:zombie,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:skeleton,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:spider,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:creeper,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:husk,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:drowned,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:stray,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:pillager,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:vindicator,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:witch,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:enderman,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:slime,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:cave_spider,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:bogged,distance=..7] minecraft:slowness 3 0 true
effect give @e[type=minecraft:zombified_piglin,distance=..7] minecraft:slowness 3 0 true
particle minecraft:snowflake ~ ~0.8 ~ 1.0 0.2 1.0 0.02 18 force @a[distance=..28]
