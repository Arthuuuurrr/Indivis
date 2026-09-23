# CapSkills BETA 0.9.30 — libération contrôlée de Rafale.
# Exige environ 4 s de canalisation/armement avant déclenchement.
scoreboard players set #rafale_ready CAPSK_TMP 0
execute if score @s CAPSK_RAFALE_CHARGE matches 80.. run scoreboard players set #rafale_ready CAPSK_TMP 1
execute if score @s CAPSK_RAFALE_VIS matches 80.. run scoreboard players set #rafale_ready CAPSK_TMP 1
execute if score @s CAPSK_RAFALE_FORCE_VIS matches 80.. run scoreboard players set #rafale_ready CAPSK_TMP 1
execute if score #rafale_ready CAPSK_TMP matches 1 run function capskills:skill/trait/cast_rafale_fire_self
execute unless score #rafale_ready CAPSK_TMP matches 1 run title @s actionbar {"text":"Rafale non prête — canalise environ 4 s.","color":"gold"}
execute unless score #rafale_ready CAPSK_TMP matches 1 at @s run playsound minecraft:block.note_block.bass player @s ~ ~ ~ 0.45 0.75 0
