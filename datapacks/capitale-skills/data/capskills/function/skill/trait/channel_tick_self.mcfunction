# CapSkills 0.9.3 — server tick for Rafale channel. Visible even without target; does not fire by itself.
execute if entity @s[tag=capskills.trait.rafale.channeling] run scoreboard players add @s CAPSK_RAFALE_CHARGE 1
execute if entity @s[tag=capskills.trait.rafale.channeling] run function capskills:visual/trait_channel_ring_self
execute if entity @s[tag=capskills.trait.rafale.channeling] run function capskills:skill/trait/visual_channel_tick_self
execute if entity @s[tag=capskills.trait.rafale.channeling] if score @s CAPSK_RAFALE_CHARGE matches 1 at @s run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.50 0.90 0
execute if entity @s[tag=capskills.trait.rafale.channeling] if score @s CAPSK_RAFALE_CHARGE matches 20 at @s run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.50 1.15 0
execute if entity @s[tag=capskills.trait.rafale.channeling] if score @s CAPSK_RAFALE_CHARGE matches 40 at @s run playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.55 1.40 0
execute if entity @s[tag=capskills.trait.rafale.channeling] if score @s CAPSK_RAFALE_CHARGE matches 80 at @s run playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.60 1.80 0
execute if entity @s[tag=capskills.trait.rafale.channeling] if score @s CAPSK_RAFALE_CHARGE matches 1..19 run title @s actionbar {"text":"Rafale : canalisation rouge — charge faible","color":"red"}
execute if entity @s[tag=capskills.trait.rafale.channeling] if score @s CAPSK_RAFALE_CHARGE matches 20..79 run title @s actionbar {"text":"Rafale : canalisation orange — maintiens","color":"gold"}
execute if entity @s[tag=capskills.trait.rafale.channeling] if score @s CAPSK_RAFALE_CHARGE matches 80.. run title @s actionbar {"text":"Rafale prête — relâche sur une cible","color":"green"}
# Safety only: if the server never receives release/cancel, stop the channel without firing after 12 seconds.
execute if entity @s[tag=capskills.trait.rafale.channeling] if score @s CAPSK_RAFALE_CHARGE matches 240.. run function capskills:skill/trait/channel_cancel_self
