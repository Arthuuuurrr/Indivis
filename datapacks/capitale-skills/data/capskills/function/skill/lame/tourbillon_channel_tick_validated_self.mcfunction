# CapSkills 0.9.103 — tick de canalisation avec objet déjà validé.
scoreboard players set @s CAPSK_LAME_TOURB_PHASE 2
scoreboard players remove @s CAPSK_LAME_TOURB_CHAN_T 1
effect give @s minecraft:slowness 1 3 true
execute at @s run particle minecraft:crit ~ ~1.0 ~ 0.55 0.35 0.55 0.025 7 force @a[distance=..20]
execute at @s run particle minecraft:sweep_attack ~ ~1.0 ~ 0.65 0.05 0.65 0.00 2 force @a[distance=..20]
execute at @s run playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.15 1.65 0
execute if score @s CAPSK_LAME_TOURB_CHAN_T matches 8.. run title @s actionbar [{"text":"Tourbillon : canalisation ","color":"red"},{"score":{"name":"@s","objective":"CAPSK_LAME_TOURB_CHAN_T"},"color":"red"},{"text":" ticks","color":"red"}]
execute if score @s CAPSK_LAME_TOURB_CHAN_T matches 1..7 run title @s actionbar {"text":"Tourbillon : relâchement imminent.","color":"red"}
execute if score @s CAPSK_LAME_TOURB_CHAN_T matches ..0 run function capskills:skill/lame/tourbillon_release_self
