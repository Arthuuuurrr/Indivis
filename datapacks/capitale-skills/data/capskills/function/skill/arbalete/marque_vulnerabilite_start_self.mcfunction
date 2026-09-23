# CapSkills 0.9.37 — Marque de vulnérabilité arbalète.
# @s = tireur. Clic gauche avec l'arbalète : prépare le prochain carreau au lieu de marquer directement la cible.
function capskills:integration/ensure_current_self
execute unless entity @s[tag=capskills.arbalete.vulnerabilite.r1] unless entity @s[tag=capskills.skill.trait_arbalete_marque_vulnerabilite_1] run title @s actionbar {"text":"Marque de vulnérabilité non débloquée.","color":"red"}
execute if entity @s[tag=capskills.arbalete.vulnerabilite.r1] if score @s CAPSK_ARBA_MARK_CD matches 1.. run title @s actionbar [{"text":"Marque de vulnérabilité en recharge : ","color":"yellow"},{"score":{"name":"@s","objective":"CAPSK_ARBA_MARK_CD"},"color":"yellow"},{"text":" s","color":"yellow"}]
execute if entity @s[tag=capskills.arbalete.vulnerabilite.r1] if score @s CAPSK_ARBA_MARK_CD matches 0 if entity @s[tag=capskills.arbalete.vulnerabilite.loaded] run title @s actionbar {"text":"Carreau de vulnérabilité déjà préparé.","color":"yellow"}
execute if entity @s[tag=capskills.arbalete.vulnerabilite.r1] if score @s CAPSK_ARBA_MARK_CD matches 0 unless entity @s[tag=capskills.arbalete.vulnerabilite.loaded] run tag @s add capskills.arbalete.vulnerabilite.loaded
execute if entity @s[tag=capskills.arbalete.vulnerabilite.r1] if score @s CAPSK_ARBA_MARK_CD matches 0 if entity @s[tag=capskills.arbalete.vulnerabilite.loaded] run scoreboard players set @s CAPSK_ARBA_MARK_T 12
execute if entity @s[tag=capskills.arbalete.vulnerabilite.r1] if score @s CAPSK_ARBA_MARK_CD matches 0 if entity @s[tag=capskills.arbalete.vulnerabilite.loaded] run scoreboard players set @s CAPSK_ARBA_MARK_CD 18
execute if entity @s[tag=capskills.arbalete.vulnerabilite.loaded] at @s run particle minecraft:electric_spark ~ ~1.1 ~ 0.35 0.25 0.35 0.04 18 force @a[distance=..32]
execute if entity @s[tag=capskills.arbalete.vulnerabilite.loaded] at @s run playsound minecraft:item.crossbow.loading_middle player @a[distance=..24] ~ ~ ~ 0.45 1.35 0
title @s actionbar {"text":"Marque de vulnérabilité prête — prochain carreau renforcé. Recharge : 18 s.","color":"yellow"}
tag @e[tag=capskills.mod_ranged_leftclick_target] remove capskills.mod_ranged_leftclick_target
