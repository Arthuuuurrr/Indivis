# PQ02-R1 — PQ02 : contexte de chaque joueur proche, aucune sélection globale du plus proche
execute if entity @s[type=minecraft:player] run function capitale:quest/le_registre_du_port/leovic/near_offer_self
execute unless entity @s[type=minecraft:player] at @s as @a[distance=..3] at @s run function capitale:quest/le_registre_du_port/leovic/near_offer_self
