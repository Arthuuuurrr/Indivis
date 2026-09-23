function capskills:integration/ensure_current_self
scoreboard players set @s CAPSK_RAFALE_CHARGE 0
tag @s remove capskills.mod_channel_rafale_ok
tag @s remove capskills.trait.rafale.channeling
execute unless entity @s[tag=capskills.trait.rafale.r1] run title @s actionbar {"text":"Rafale réglementaire non débloquée.","color":"red"}
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 1.. run title @s actionbar {"text":"Rafale réglementaire en recharge.","color":"yellow"}
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 run tag @s add capskills.mod_channel_rafale_ok
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 run tag @s add capskills.trait.rafale.channeling
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 at @s run playsound minecraft:block.beacon.power_select player @s ~ ~ ~ 0.55 1.45 0
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 at @s run particle minecraft:end_rod ~ ~1.25 ~ 0.35 0.45 0.35 0.02 18 force @a[distance=..32]
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 at @s anchored eyes rotated as @s positioned ^ ^ ^1.25 run particle minecraft:crit ~ ~ ~ 0.16 0.16 0.16 0.04 18 force @a[distance=..32]
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 run function capskills:visual/trait_channel_ring_self
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 at @s run particle minecraft:end_rod ~ ~0.18 ~ 1.10 0.02 1.10 0.00 32 force @a[distance=..32]
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 run title @s actionbar {"text":"Rafale : canalisation rouge...","color":"yellow"}
