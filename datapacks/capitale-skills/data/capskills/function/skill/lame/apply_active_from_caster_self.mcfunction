execute unless entity @e[tag=capskills.lame_target,limit=1,sort=nearest] run title @s actionbar {"text":"Cible de fente perdue.","color":"red"}
execute at @e[tag=capskills.lame_target,limit=1,sort=nearest] run particle minecraft:sweep_attack ~ ~1.0 ~ 0.20 0.30 0.20 0.00 2 force @a[distance=..24]
execute at @e[tag=capskills.lame_target,limit=1,sort=nearest] run particle minecraft:damage_indicator ~ ~1.0 ~ 0.25 0.35 0.25 0.05 6 force @a[distance=..24]
execute at @e[tag=capskills.lame_target,limit=1,sort=nearest] run playsound minecraft:entity.player.attack.strong player @a[distance=..20] ~ ~ ~ 0.75 1.10 0
execute if entity @e[tag=capskills.lame_target,limit=1,sort=nearest] run damage @e[tag=capskills.lame_target,limit=1,sort=nearest] 6 minecraft:generic by @s
execute if entity @s[tag=capskills.lame.breche.r1] as @e[tag=capskills.lame_target,limit=1,sort=nearest,type=!minecraft:player] run effect give @s minecraft:weakness 4 0 true
execute at @e[tag=capskills.lame_target,limit=1,sort=nearest] run effect give @e[tag=capskills.lame_target,limit=1,sort=nearest] minecraft:glowing 2 0 true
scoreboard players set @s CAPSK_LAME_CD 10
execute unless entity @s[tag=capskills.lame.breche.r1] run title @s actionbar {"text":"Fente impériale exécutée. Recharge : 10 s.","color":"red"}
execute if entity @s[tag=capskills.lame.breche.r1] run title @s actionbar {"text":"Fente impériale : brèche appliquée. Recharge : 10 s.","color":"red"}
