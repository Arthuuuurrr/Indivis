# CapSkills 0.9.30 — Rafale : 5 impacts espacés plus nerveux, lisibles et esquivables.
scoreboard players add @s CAPSK_RAFALE_BURST_T 1
# Traces discrètes pendant l'espacement entre impacts, sans particules persistantes à chaque tick.
execute if score @s CAPSK_RAFALE_BURST_T matches 1..49 if score @s CAPSK_RAFALE_BURST_T matches 1 run function capskills:visual/rafale_tracer_self
execute if score @s CAPSK_RAFALE_BURST_T matches 11 run title @s actionbar {"text":"Rafale : impact 2/5","color":"yellow"}
execute if score @s CAPSK_RAFALE_BURST_T matches 11 run function capskills:skill/trait/burst_shot_self
execute if score @s CAPSK_RAFALE_BURST_T matches 22 run title @s actionbar {"text":"Rafale : impact 3/5","color":"yellow"}
execute if score @s CAPSK_RAFALE_BURST_T matches 22 run function capskills:skill/trait/burst_shot_self
execute if score @s CAPSK_RAFALE_BURST_T matches 33 run title @s actionbar {"text":"Rafale : impact 4/5","color":"yellow"}
execute if score @s CAPSK_RAFALE_BURST_T matches 33 run function capskills:skill/trait/burst_shot_self
execute if score @s CAPSK_RAFALE_BURST_T matches 44 run title @s actionbar {"text":"Rafale : impact 5/5","color":"yellow"}
execute if score @s CAPSK_RAFALE_BURST_T matches 44 run function capskills:skill/trait/burst_shot_self
execute if score @s CAPSK_RAFALE_BURST_T matches 49.. run function capskills:skill/trait/burst_end_self
