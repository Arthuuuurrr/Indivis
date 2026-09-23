# @s = lanceur. Bouclier personnel si aucun allié n'est visé.
# 0.6 : Bastion renforce le tank personnel ; Brise-ligne ajoute une courte pression offensive.
effect give @s minecraft:absorption 20 0 true
execute if entity @s[tag=capskills.rempart.posture.r1] run effect give @s minecraft:absorption 22 0 true
execute if score @s CAPSK_BASTION_RANK matches 1.. run effect give @s minecraft:absorption 24 1 true
execute if score @s CAPSK_BASTION_RANK matches 1.. run effect give @s minecraft:regeneration 4 0 true
execute if score @s CAPSK_BASTION_RANK matches 2.. run effect give @s minecraft:resistance 5 0 true
execute if score @s CAPSK_BASTION_RANK matches 2.. run effect give @s minecraft:absorption 28 1 true
execute if score @s CAPSK_BRISE_RANK matches 1.. run effect give @s minecraft:strength 4 0 true
execute if score @s CAPSK_BRISE_RANK matches 2.. run effect give @s minecraft:speed 4 0 true
execute at @s run particle minecraft:enchant ~ ~1 ~ 1.1 0.8 1.1 0.08 36 force @a[distance=..12]
execute at @s run particle minecraft:crit ~ ~1.1 ~ 0.85 0.65 0.85 0.04 18 force @a[distance=..12]
execute at @s run playsound minecraft:block.respawn_anchor.charge player @s ~ ~ ~ 0.7 1.1 0
scoreboard players set @s CAPSK_REMP_SELF_CD 18
title @s actionbar {"text":"Rempart personnel activé.","color":"blue"}


# 0.8.29j : plus de provocation automatique au clic droit du Rempart.
# La provocation MMO se teste uniquement via clic gauche / hit avec le Relais du Rempart en main.
