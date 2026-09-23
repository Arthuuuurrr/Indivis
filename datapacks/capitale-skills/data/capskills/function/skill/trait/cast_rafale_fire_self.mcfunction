function capskills:integration/ensure_current_self
execute unless entity @s[tag=capskills.trait.rafale.r1] run title @s actionbar {"text":"Rafale réglementaire non débloquée.","color":"red"}
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 1.. run title @s actionbar {"text":"Rafale réglementaire en recharge.","color":"yellow"}
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 run function capskills:skill/trait/probe_target_on_release_self
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 if score #hit CAPSK_TMP matches 1.. run function capskills:skill/trait/burst_start_self
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 if score #hit CAPSK_TMP matches 0 run title @s actionbar {"text":"Rafale annulée : aucune cible dans l’axe.","color":"yellow"}
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 if score #hit CAPSK_TMP matches 0 at @s run playsound minecraft:block.fire.extinguish player @s ~ ~ ~ 0.45 1.40 0
execute if score #hit CAPSK_TMP matches 0 at @s anchored eyes rotated as @s positioned ^ ^ ^1.50 run particle minecraft:smoke ~ ~ ~ 0.15 0.15 0.15 0.02 14 force @a[distance=..32]
tag @e[tag=capskills.trait_target] remove capskills.trait_target
scoreboard players set #hit CAPSK_TMP 0
