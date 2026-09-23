# CapSkills 0.9.78 EXP — fin propre du Tourbillon actif.
tag @s remove capskills.lame.tourbillon.animation_pending
tag @s remove capskills.lame.tourbillon.spinning
tag @s remove capskills.tourbillon_caster
tag @s remove capskills.lame.tourbillon.armed
tag @s remove capskills.lame.tourbillon.channeling
tag @s remove capskills.lame.tourbillon.just_started
scoreboard players set @s CAPSK_LAME_TOURB_PHASE 6
scoreboard players set @s CAPSK_LAME_TOURB_SPIN_T 0
scoreboard players set @s CAPSK_LAME_TOURB_PULSE 0
scoreboard players set @s CAPSK_LAME_TOURB_CHAN_T 0
execute at @s run particle minecraft:cloud ~ ~0.6 ~ 0.75 0.15 0.75 0.02 12 force @a[distance=..24]
