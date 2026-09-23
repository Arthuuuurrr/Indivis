# @s = lanceur. Cible = capskills.trait_target.
execute unless entity @e[tag=capskills.trait_target,limit=1,sort=nearest] run title @s actionbar {"text":"Cible de rafale perdue.","color":"red"}
execute at @e[tag=capskills.trait_target,limit=1,sort=nearest] run particle minecraft:crit ~ ~1.0 ~ 0.45 0.60 0.45 0.10 30 force @a[distance=..32]
execute at @e[tag=capskills.trait_target,limit=1,sort=nearest] run particle minecraft:damage_indicator ~ ~1.0 ~ 0.18 0.25 0.18 0.04 4 force @a[distance=..32]
execute at @e[tag=capskills.trait_target,limit=1,sort=nearest] run playsound minecraft:entity.arrow.hit player @a[distance=..24] ~ ~ ~ 0.55 1.20 0
execute if entity @e[tag=capskills.trait_target,limit=1,sort=nearest] run damage @e[tag=capskills.trait_target,limit=1,sort=nearest] 3 minecraft:arrow by @s
execute if entity @s[tag=capskills.trait.rafale.r2] run effect give @e[tag=capskills.trait_target,limit=1,sort=nearest] minecraft:glowing 3 0 true
