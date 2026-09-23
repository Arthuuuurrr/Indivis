# CapSkills 0.9.13 — Salve préparée : hook usageTick conservé, fallback datapack désormais prioritaire.
# Le tag capskills.arbalete.salve.usage_tick est posé uniquement par le hook Item.usageTick.
function capskills:integration/ensure_current_self
scoreboard players set @s CAPSK_CROSS_USED 0
execute if entity @s[tag=capskills.arbalete.salve.usage_tick] unless entity @s[tag=capskills.arbalete.salve.r1] run title @s actionbar {"text":"Salve préparée non débloquée.","color":"gray"}
execute if entity @s[tag=capskills.arbalete.salve.usage_tick] if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 1.. run title @s actionbar [{"text":"Salve préparée en recharge : ","color":"yellow"},{"score":{"name":"@s","objective":"CAPSK_CROSS_CD"},"color":"yellow"},{"text":" s","color":"yellow"}]
execute if entity @s[tag=capskills.arbalete.salve.usage_tick] if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 run scoreboard players set @s CAPSK_CROSS_CHARGE 0
execute if entity @s[tag=capskills.arbalete.salve.usage_tick] if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 run scoreboard players set @s CAPSK_CROSS_READY 0
execute if entity @s[tag=capskills.arbalete.salve.usage_tick] if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 run tag @s add capskills.arbalete.salve.channeling
execute if entity @s[tag=capskills.arbalete.salve.usage_tick] if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 at @s run playsound minecraft:item.crossbow.loading_start player @a[distance=..24] ~ ~ ~ 0.65 0.70 0
execute if entity @s[tag=capskills.arbalete.salve.usage_tick] if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 at @s run particle minecraft:crit ~ ~0.45 ~ 0.70 0.02 0.70 0.00 12 force @a[distance=..32]
execute if entity @s[tag=capskills.arbalete.salve.usage_tick] if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 run title @s actionbar {"text":"Salve préparée : verrouillage long...","color":"yellow"}
tag @s remove capskills.arbalete.salve.usage_tick
