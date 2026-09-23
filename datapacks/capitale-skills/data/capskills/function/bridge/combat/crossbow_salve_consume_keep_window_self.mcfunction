# CapSkills 0.9.20 — consommation de Salve sans retirer la fenêtre just_shot.
# @s = arbalétrier. Utilisé par le fallback advancement : on retire l'état armé,
# mais on garde just_shot/pending_clear quelques ticks pour tagger le projectile s'il apparaît après la récompense advancement.
scoreboard players set @s CAPSK_CROSS_READY 0
scoreboard players set @s CAPSK_CROSS_USED 0
scoreboard players set @s CAPSK_CROSS_SHOT_T 8
tag @s remove capskills.arbalete.salve.ready
tag @s remove capskills.arbalete.salve.channeling
tag @s remove capskills.arbalete.salve.forced_channel
tag @s add capskills.arbalete.salve.pending_clear
tag @s add capskills.arbalete.salve.just_shot
title @s actionbar {"text":"Salve libérée — fenêtre projectile active","color":"yellow"}
