# CapSkills 0.9.20 — fenêtre courte de tag projectile après minecraft:shot_crossbow.
function capskills:bridge/combat/crossbow_tag_near_projectiles_self
scoreboard players remove @s CAPSK_CROSS_ADV_T 1
execute if score @s CAPSK_CROSS_ADV_T matches 0 run function capskills:bridge/combat/crossbow_adv_window_clear_self
