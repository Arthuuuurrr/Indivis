execute if entity @s[tag=capskills.trait.rafale.channeling] run function capskills:skill/trait/cast_rafale_fire_checked_self
tag @s remove capskills.trait.rafale.channeling
tag @s remove capskills.mod_channel_rafale_ok
scoreboard players set @s CAPSK_RAFALE_CHARGE 0

function capskills:skill/trait/visual_channel_clear_self
