execute unless entity @s[tag=capskills.trait.rafale.r2] run title @s actionbar {"text":"Rafale réglementaire terminée. Recharge : 22 s.","color":"yellow"}
execute if entity @s[tag=capskills.trait.rafale.r2] run title @s actionbar {"text":"Rafale réglementaire II terminée. Recharge : 18 s.","color":"yellow"}
tag @s remove capskills.trait.rafale.bursting
scoreboard players set @s CAPSK_RAFALE_BURST_T 0
scoreboard players set @s CAPSK_RAFALE_BURST_SHOT 0
tag @e[tag=capskills.trait_target] remove capskills.trait_target
