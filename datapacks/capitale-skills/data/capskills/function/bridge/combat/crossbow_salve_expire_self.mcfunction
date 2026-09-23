# CapSkills 0.9.16 — expiration de la fenêtre de tir Salve.
# Sécurité : si le hook projectile n’a pas consommé la salve, on vide l’état pour éviter que tous les tirs restent chargés.
scoreboard players set @s CAPSK_CROSS_READY 0
scoreboard players set @s CAPSK_CROSS_SHOT_T 0
scoreboard players set @s CAPSK_CROSS_USED 0
tag @s remove capskills.arbalete.salve.ready
tag @s remove capskills.arbalete.salve.channeling
tag @s remove capskills.arbalete.salve.forced_channel
tag @s remove capskills.arbalete.salve.pending_clear
tag @s remove capskills.arbalete.salve.just_shot
title @s actionbar {"text":"Salve consommée — recharge engagée","color":"yellow"}
