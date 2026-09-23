# CapSkills BETA 0.9.6 — charge visible garantie pour Rafale.
# @s = archer. Visuel seulement, aucun déclenchement de sort/cooldown.
function capskills:integration/ensure_current_self
execute unless entity @s[tag=capskills.trait.rafale.r1] run title @s actionbar {"text":"Rafale réglementaire non débloquée.","color":"gray"}
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 1.. run title @s actionbar {"text":"Rafale réglementaire en recharge.","color":"yellow"}
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 run tag @s add capskills.trait.rafale.visual_channel
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 run scoreboard players add @s CAPSK_RAFALE_VIS 1
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 run function capskills:visual/rafale_charge_persistent_self
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 if score @s CAPSK_RAFALE_VIS matches 1..79 run title @s actionbar {"text":"Rafale : charge en cours...","color":"gold"}
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 if score @s CAPSK_RAFALE_VIS matches 80.. run title @s actionbar {"text":"Rafale chargée — relâche sur une cible","color":"green"}
execute if score @s CAPSK_RAFALE_VIS matches 140.. run scoreboard players set @s CAPSK_RAFALE_VIS 100
