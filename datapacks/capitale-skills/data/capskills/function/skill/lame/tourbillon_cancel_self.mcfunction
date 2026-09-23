# CapSkills 0.9.72 EXP — annulation de sécurité du Tourbillon.
# Normalement peu utilisée : la canalisation ne dépend plus du maintien sneak.
tag @s remove capskills.lame.tourbillon.animation_pending
tag @s remove capskills.lame.tourbillon.channeling
tag @s remove capskills.lame.tourbillon.armed
scoreboard players set @s CAPSK_LAME_TOURB_CHAN_T 0
execute if score @s CAPSK_LAME_TOURB_CD matches ..0 run scoreboard players set @s CAPSK_LAME_TOURB_CD 4
execute at @s run particle minecraft:smoke ~ ~1.0 ~ 0.45 0.20 0.45 0.02 10 force @a[distance=..18]
execute at @s run playsound minecraft:block.fire.extinguish player @a[distance=..18] ~ ~ ~ 0.40 1.35 0
