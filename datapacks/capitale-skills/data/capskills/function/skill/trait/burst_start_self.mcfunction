# 0.9.30 — démarre une Rafale en 5 impacts espacés.
# Espacement : 11/22/33/44 ticks, un peu plus nerveux après canalisation allongée.
scoreboard players set @s CAPSK_RAFALE_BURST_T 0
scoreboard players set @s CAPSK_RAFALE_BURST_SHOT 0
tag @s add capskills.trait.rafale.bursting
execute at @s run function capskills:visual/activation/rafale_release_self
execute unless entity @s[tag=capskills.trait.rafale.r2] run scoreboard players set @s CAPSK_RAFALE_CD 22
execute if entity @s[tag=capskills.trait.rafale.r2] run scoreboard players set @s CAPSK_RAFALE_CD 18
execute at @s run playsound minecraft:entity.arrow.shoot player @a[distance=..32] ~ ~ ~ 0.85 1.45 0
execute at @s run particle minecraft:end_rod ~ ~1.35 ~ 0.35 0.45 0.35 0.03 24 force @a[distance=..32]
title @s actionbar {"text":"Rafale : impact 1/5","color":"yellow"}
function capskills:skill/trait/burst_shot_self
