# @s = cible touchée par la frappe de sortie.
effect give @s minecraft:slowness 4 1 true
effect give @s minecraft:glowing 4 0 true
execute at @s run particle minecraft:sculk_soul ~ ~1.0 ~ 0.35 0.45 0.35 0.03 20 force @a[distance=..32]
execute at @s run playsound minecraft:entity.enderman.hurt player @a[distance=..28] ~ ~ ~ 0.40 1.65 0
execute if entity @s[type=minecraft:player] run title @s actionbar {"text":"Frappe de sortie — ralenti","color":"dark_gray"}
title @a[tag=capskills.shadow_caster,limit=1,sort=nearest] actionbar {"text":"Frappe de sortie appliquée.","color":"dark_gray"}
