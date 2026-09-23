effect give @s minecraft:slowness 4 1 true
effect give @s minecraft:glowing 4 0 true
execute at @s run particle minecraft:crit ~ ~1.0 ~ 0.35 0.35 0.35 0.04 18 force @a[distance=..32]
execute at @s run playsound minecraft:entity.arrow.hit_player player @a[distance=..32] ~ ~ ~ 0.45 1.25 0
execute if entity @s[type=minecraft:player] run title @s actionbar {"text":"Flèche d’entrave — ralenti","color":"yellow"}
title @a[tag=capskills.trait_caster,limit=1,sort=nearest] actionbar {"text":"Flèche d’entrave appliquée.","color":"yellow"}
