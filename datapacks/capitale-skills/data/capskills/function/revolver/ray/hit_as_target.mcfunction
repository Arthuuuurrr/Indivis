scoreboard players set @a[tag=capskills.revolver.shooter,limit=1] CAPREV_HIT 1
particle minecraft:damage_indicator ~ ~1 ~ 0.15 0.2 0.15 0.05 5 force
playsound minecraft:entity.generic.hurt player @a[distance=..24] ~ ~ ~ 0.45 1.35
# Tir à une main : portée 28 blocs, dégâts élevés.
execute if entity @a[tag=capskills.revolver.shooter,tag=!capskills.revolver.dual,tag=capskills.revolver.tier_iron,limit=1] run damage @s 6 minecraft:player_attack by @a[tag=capskills.revolver.shooter,limit=1]
execute if entity @a[tag=capskills.revolver.shooter,tag=!capskills.revolver.dual,tag=capskills.revolver.tier_gold,limit=1] run damage @s 6.5 minecraft:player_attack by @a[tag=capskills.revolver.shooter,limit=1]
execute if entity @a[tag=capskills.revolver.shooter,tag=!capskills.revolver.dual,tag=capskills.revolver.tier_diamond,limit=1] run damage @s 7 minecraft:player_attack by @a[tag=capskills.revolver.shooter,limit=1]
execute if entity @a[tag=capskills.revolver.shooter,tag=!capskills.revolver.dual,tag=capskills.revolver.tier_netherite,limit=1] run damage @s 8 minecraft:player_attack by @a[tag=capskills.revolver.shooter,limit=1]
# Double revolver : portée 18 blocs, précision et dégâts réduits.
execute if entity @a[tag=capskills.revolver.shooter,tag=capskills.revolver.dual,tag=capskills.revolver.tier_iron,limit=1] run damage @s 4.25 minecraft:player_attack by @a[tag=capskills.revolver.shooter,limit=1]
execute if entity @a[tag=capskills.revolver.shooter,tag=capskills.revolver.dual,tag=capskills.revolver.tier_gold,limit=1] run damage @s 4.5 minecraft:player_attack by @a[tag=capskills.revolver.shooter,limit=1]
execute if entity @a[tag=capskills.revolver.shooter,tag=capskills.revolver.dual,tag=capskills.revolver.tier_diamond,limit=1] run damage @s 5 minecraft:player_attack by @a[tag=capskills.revolver.shooter,limit=1]
execute if entity @a[tag=capskills.revolver.shooter,tag=capskills.revolver.dual,tag=capskills.revolver.tier_netherite,limit=1] run damage @s 5.75 minecraft:player_attack by @a[tag=capskills.revolver.shooter,limit=1]
