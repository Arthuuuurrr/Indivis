# @s = cible marquée par l'arbalétrier.
tag @s add capskills.arbalete.vulnerable
scoreboard players set @s CAPSK_ARBA_VULN_T 160
effect give @s minecraft:glowing 8 0 true
effect give @s minecraft:weakness 4 0 true
execute at @s run particle minecraft:electric_spark ~ ~1.1 ~ 0.45 0.35 0.45 0.04 22 force @a[distance=..40]
execute at @s run particle minecraft:enchanted_hit ~ ~1.1 ~ 0.35 0.30 0.35 0.03 18 force @a[distance=..40]
execute at @s run playsound minecraft:item.crossbow.loading_end player @a[distance=..36] ~ ~ ~ 0.45 0.75 0
execute if entity @s[type=minecraft:player] run title @s actionbar {"text":"Marque de vulnérabilité appliquée","color":"yellow"}
title @a[tag=capskills.arbalete.mark_caster,limit=1,sort=nearest] actionbar {"text":"Marque de vulnérabilité posée. Recharge : 18 s.","color":"yellow"}
