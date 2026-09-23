execute store result score #roll CAPSK_TMP run random value 1..100
execute if entity @s[tag=capskills.forge.r2] if score #roll CAPSK_TMP matches ..40 run give @s minecraft:string 1
execute if entity @s[tag=capskills.forge.r2] if score #roll CAPSK_TMP matches ..20 run give @s minecraft:stick 1
execute unless entity @s[tag=capskills.forge.r2] if entity @s[tag=capskills.forge.r1] if score #roll CAPSK_TMP matches ..25 run give @s minecraft:string 1
execute if score #roll CAPSK_TMP matches ..40 run title @s actionbar {"text":"Forgeron : composant récupéré.","color":"gray"}
