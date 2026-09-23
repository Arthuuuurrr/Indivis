# 0.9.39 — Chaman : sneak + clic droit = Totem de Guérison.
execute unless entity @s[tag=capskills.chaman.totem.guerison.r1] unless entity @s[tag=capskills.skill.chaman_totem_guerison_1] run title @s actionbar {"text":"Totem de Guérison non débloqué.","color":"red"}
execute if entity @s[tag=capskills.skill.chaman_totem_guerison_1] run tag @s add capskills.chaman.totem.guerison.r1
execute if entity @s[tag=capskills.chaman.totem.guerison.r1] if score @s CAPSK_CHAMAN_TOTEM_CD matches 1.. run title @s actionbar [{"text":"Totem chamanique en recharge : ","color":"green"},{"score":{"name":"@s","objective":"CAPSK_CHAMAN_TOTEM_CD"},"color":"green"},{"text":" s","color":"green"}]
execute if entity @s[tag=capskills.chaman.totem.guerison.r1] if score @s CAPSK_CHAMAN_TOTEM_CD matches 0 run function capskills:skill/chaman/totem_guerison_cast_self
