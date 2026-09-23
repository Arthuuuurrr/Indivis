execute if entity @s[tag=capskills.trait.rafale.channeling] run title @s actionbar {"text":"Rafale interrompue.","color":"gray"}
tag @s remove capskills.trait.rafale.channeling
tag @s remove capskills.mod_channel_rafale_ok
scoreboard players set @s CAPSK_RAFALE_CHARGE 0
