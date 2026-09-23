# CapSkills 0.9.68 EXP — expiration de l'ancien état armé si présent après migration.
tag @s remove capskills.lame.tourbillon.animation_pending
tag @s remove capskills.lame.tourbillon.armed
tag @s remove capskills.lame.tourbillon.channeling
scoreboard players set @s CAPSK_LAME_TOURB_ARM_T 0
title @s actionbar {"text":"Tourbillon dissipé : ancien état armé nettoyé.","color":"gray"}
