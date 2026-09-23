execute store result score #roll CAPSK_TMP run random value 1..100
execute if entity @s[tag=capskills.forge.r2] if score #roll CAPSK_TMP matches ..38 run give @s minecraft:string 1
execute if entity @s[tag=capskills.forge.r2] if score #roll CAPSK_TMP matches ..18 run give @s minecraft:iron_ingot 1
execute unless entity @s[tag=capskills.forge.r2] if entity @s[tag=capskills.forge.r1] if score #roll CAPSK_TMP matches ..24 run give @s minecraft:string 1
execute if score #roll CAPSK_TMP matches ..38 run title @s actionbar {"text":"Forgeron : composant récupéré.","color":"gray"}
