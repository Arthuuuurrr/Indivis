# 0.9.39 — Chaman : clic droit simple = Totem de Protection.
execute unless entity @s[tag=capskills.chaman.totem.protection.r1] unless entity @s[tag=capskills.skill.chaman_totem_protection_1] run title @s actionbar {"text":"Totem de Protection non débloqué.","color":"red"}
execute if entity @s[tag=capskills.skill.chaman_totem_protection_1] run tag @s add capskills.chaman.totem.protection.r1
execute if entity @s[tag=capskills.chaman.totem.protection.r1] if score @s CAPSK_CHAMAN_TOTEM_CD matches 1.. run title @s actionbar [{"text":"Totem chamanique en recharge : ","color":"dark_aqua"},{"score":{"name":"@s","objective":"CAPSK_CHAMAN_TOTEM_CD"},"color":"dark_aqua"},{"text":" s","color":"dark_aqua"}]
execute if entity @s[tag=capskills.chaman.totem.protection.r1] if score @s CAPSK_CHAMAN_TOTEM_CD matches 0 run function capskills:skill/chaman/totem_protection_cast_self
