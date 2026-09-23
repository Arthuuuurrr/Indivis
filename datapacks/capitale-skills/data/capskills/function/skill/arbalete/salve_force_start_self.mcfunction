# CapSkills 0.9.13 — Salve préparée : fallback fiable, calqué sur le visuel Rafale.
# @s = joueur. Déclenchement volontairement datapack : Salve débloquée + arbalète en main + sneak + cooldown prêt.
function capskills:integration/ensure_current_self
scoreboard players set @s CAPSK_CROSS_USED 0
# 0.9.14 — sécurité : certains profils peuvent avoir le marqueur UI sans le tag fonctionnel.
execute if entity @s[tag=capskills.skill.trait_salve_preparee_1] run tag @s add capskills.arbalete.salve.r1
execute if entity @s[tag=capskills.skill.trait_arbalete_antarmure_1] run tag @s add capskills.arbalete.antiarmor.r1
execute if entity @s[tag=capskills.skill.trait_arbalete_stabilisation_1] run tag @s add capskills.arbalete.stabilisation.r1
execute if entity @s[tag=capskills.skill.trait_arbalete_percearmure_1] run tag @s add capskills.arbalete.percearmure.r1
execute if entity @s[tag=capskills.skill.trait_arbalete_carreau_arret_1] run tag @s add capskills.arbalete.carreau_arret.r1
execute unless entity @s[tag=capskills.arbalete.salve.r1] run title @s actionbar {"text":"Salve préparée non débloquée.","color":"gray"}
execute if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 1.. run title @s actionbar [{"text":"Salve préparée en recharge : ","color":"yellow"},{"score":{"name":"@s","objective":"CAPSK_CROSS_CD"},"color":"yellow"},{"text":" s","color":"yellow"}]
execute if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 run scoreboard players set @s CAPSK_CROSS_CHARGE 0
execute if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 run scoreboard players set @s CAPSK_CROSS_READY 0
execute if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 run tag @s add capskills.arbalete.salve.channeling
execute if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 run tag @s add capskills.arbalete.salve.forced_channel
execute if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 at @s run playsound minecraft:item.crossbow.loading_start player @a[distance=..28] ~ ~ ~ 0.70 0.60 0
execute if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 at @s run function capskills:visual/arbalete_charge_small_self
execute if entity @s[tag=capskills.arbalete.salve.r1] if score @s CAPSK_CROSS_CD matches 0 run title @s actionbar {"text":"Salve préparée : canalisation lourde...","color":"yellow"}
