# CapSkills 0.9.9 — activation arbalète anti-armure : impact métallique.
execute at @e[tag=capskills.crossbow_target,limit=1,sort=nearest] run particle minecraft:crit ~ ~1.0 ~ 0.45 0.30 0.45 0.04 18 force @a[distance=..36]
execute at @e[tag=capskills.crossbow_target,limit=1,sort=nearest] run particle minecraft:enchanted_hit ~ ~1.05 ~ 0.35 0.25 0.35 0.02 12 force @a[distance=..36]
execute at @e[tag=capskills.crossbow_target,limit=1,sort=nearest] run playsound minecraft:block.chain.hit player @a[distance=..36] ~ ~ ~ 0.50 0.90 0
