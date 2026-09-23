# CapSkills 0.9.12 — fin/interruption de canalisation Salve préparée.
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run tag @s add capskills.arbalete.salve.ready
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run scoreboard players set @s CAPSK_CROSS_USED 0
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. at @s run function capskills:visual/activation/arbalete_salve_ready_self
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run scoreboard players set @s CAPSK_CROSS_CD 30
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run title @s actionbar {"text":"Salve préparée armée — un seul carreau accéléré","color":"green"}
execute if entity @s[tag=capskills.arbalete.salve.channeling] unless score @s CAPSK_CROSS_CHARGE matches 100.. run function capskills:skill/arbalete/salve_cancel_self
execute if entity @s[tag=capskills.arbalete.salve.channeling] if score @s CAPSK_CROSS_CHARGE matches 100.. run tag @s remove capskills.arbalete.salve.channeling
tag @s remove capskills.arbalete.salve.usage_tick
