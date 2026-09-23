# CapSkills 0.9.109 — trois rotations Better Combat complètes, un impact par rotation.
scoreboard players set @s CAPSK_LAME_TOURB_PHASE 4
scoreboard players remove @s CAPSK_LAME_TOURB_SPIN_T 1
scoreboard players add @s CAPSK_LAME_TOURB_PULSE 1
# Le premier marqueur est posé au relâchement. Les deux suivants démarrent
# exactement après un cycle visuel complet (~26 ticks avec le timing natif).
execute if score @s CAPSK_LAME_TOURB_PULSE matches 26 run tag @s add capskills.lame.tourbillon.animation_pending
execute if score @s CAPSK_LAME_TOURB_PULSE matches 52 run tag @s add capskills.lame.tourbillon.animation_pending
execute at @s run particle minecraft:sweep_attack ~ ~1.0 ~ 1.35 0.15 1.35 0.00 4 force @a[distance=..24]
execute at @s run particle minecraft:crit ~ ~1.0 ~ 1.10 0.35 1.10 0.03 8 force @a[distance=..24]
# Impact visuel natif : 18 ticks * 0,25 = 4,5 ticks, arrondi à 5.
execute if score @s CAPSK_LAME_TOURB_PULSE matches 5 at @s run function capskills:skill/lame/tourbillon_pulse_self
execute if score @s CAPSK_LAME_TOURB_PULSE matches 31 at @s run function capskills:skill/lame/tourbillon_pulse_self
execute if score @s CAPSK_LAME_TOURB_PULSE matches 57 at @s run function capskills:skill/lame/tourbillon_pulse_self
execute if score @s CAPSK_LAME_TOURB_PULSE matches 5 run title @s actionbar {"text":"Tourbillon : rotation I — impact","color":"red"}
execute if score @s CAPSK_LAME_TOURB_PULSE matches 31 run title @s actionbar {"text":"Tourbillon : rotation II — impact","color":"red"}
execute if score @s CAPSK_LAME_TOURB_PULSE matches 57 run title @s actionbar {"text":"Tourbillon : rotation III — impact","color":"red"}
execute if score @s CAPSK_LAME_TOURB_SPIN_T matches ..0 run function capskills:skill/lame/tourbillon_end_self
