execute store result score #roll CAPSK_TMP run random value 1..100
execute if entity @s[tag=capskills.forge.r2] if score #roll CAPSK_TMP matches ..34 run give @s minecraft:iron_ingot 1
execute unless entity @s[tag=capskills.forge.r2] if entity @s[tag=capskills.forge.r1] if score #roll CAPSK_TMP matches ..20 run give @s minecraft:iron_ingot 1
execute if score #roll CAPSK_TMP matches ..34 run title @s actionbar {"text":"Forgeron : renfort récupéré.","color":"gray"}
