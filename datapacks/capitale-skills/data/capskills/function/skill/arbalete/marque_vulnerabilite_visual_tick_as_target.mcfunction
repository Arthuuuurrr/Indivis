# @s = cible vulnérable.
scoreboard players remove @s CAPSK_ARBA_VULN_T 1
execute if score @s CAPSK_ARBA_VULN_T matches 1.. at @s run particle minecraft:enchanted_hit ~ ~1.05 ~ 0.20 0.30 0.20 0.01 3 force @a[distance=..32]
execute if score @s CAPSK_ARBA_VULN_T matches 1.. if entity @s[type=minecraft:player] run title @s actionbar {"text":"Marqué — prochain carreau dangereux","color":"yellow"}
execute if score @s CAPSK_ARBA_VULN_T matches ..0 run tag @s remove capskills.arbalete.vulnerable
