# CapSkills 0.9.37 — Coup d'arrêt d'Ombre.
# @s = cible frappée avec le Sceau des Ombres ; le lanceur porte capskills.shadow_caster.
# Interrompt les canalisations CapSkills connues sans retirer les sorts déjà libérés.

tag @s remove capskills.trait.rafale.channeling
tag @s remove capskills.trait.rafale.force_visual
tag @s remove capskills.arbalete.salve.channeling
scoreboard players set @s CAPSK_RAFALE_CHARGE 0
scoreboard players set @s CAPSK_RAFALE_VIS 0
scoreboard players set @s CAPSK_RAFALE_FORCE_VIS 0
scoreboard players set @s CAPSK_CROSS_CHARGE 0
scoreboard players set @s CAPSK_CROSS_READY 0

effect give @s minecraft:mining_fatigue 2 0 true
effect give @s minecraft:weakness 2 0 true
effect give @s minecraft:glowing 2 0 true
execute at @s run particle minecraft:sculk_soul ~ ~1.0 ~ 0.35 0.45 0.35 0.03 24 force @a[distance=..32]
execute at @s run playsound minecraft:block.respawn_anchor.deplete player @a[distance=..32] ~ ~ ~ 0.45 1.65 0
execute if entity @s[type=minecraft:player] run title @s actionbar {"text":"Coup d'arrêt — canalisation interrompue","color":"dark_gray"}
title @a[tag=capskills.shadow_caster,limit=1,sort=nearest] actionbar {"text":"Coup d'arrêt exécuté. Recharge : 16 s.","color":"dark_gray"}
