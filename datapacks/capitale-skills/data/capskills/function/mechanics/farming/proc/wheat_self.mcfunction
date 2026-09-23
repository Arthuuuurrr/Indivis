# 0.8.30-rc4 — récolte supplémentaire + chance de plat préparé.
execute store result score #roll CAPSK_TMP run random value 1..100
execute if entity @s[tag=capskills.farming.r2] if score #roll CAPSK_TMP matches ..45 run give @s minecraft:wheat 2
execute unless entity @s[tag=capskills.farming.r2] if entity @s[tag=capskills.farming.r1] if score #roll CAPSK_TMP matches ..25 run give @s minecraft:wheat 2
execute if score #roll CAPSK_TMP matches ..45 run title @s actionbar {"text":"Agriculteur : récolte supplémentaire.","color":"green"}
execute store result score #food CAPSK_TMP run random value 1..100
execute if entity @s[tag=capskills.farming.r2] if score #food CAPSK_TMP matches ..8 run give @s minecraft:pumpkin_pie 1
execute if entity @s[tag=capskills.farming.r2] if score #food CAPSK_TMP matches 9..24 run give @s minecraft:bread 1
execute unless entity @s[tag=capskills.farming.r2] if entity @s[tag=capskills.farming.r1] if score #food CAPSK_TMP matches ..10 run give @s minecraft:bread 1
execute if entity @s[tag=capskills.farming.r2] if score #food CAPSK_TMP matches ..24 run title @s actionbar {"text":"Agriculteur : nourriture préparée.","color":"gold"}
execute unless entity @s[tag=capskills.farming.r2] if entity @s[tag=capskills.farming.r1] if score #food CAPSK_TMP matches ..10 run title @s actionbar {"text":"Agriculteur : nourriture préparée.","color":"gold"}
