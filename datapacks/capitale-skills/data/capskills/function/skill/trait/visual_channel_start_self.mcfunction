# CapSkills 0.9.5 — début visuel de canalisation.
tag @s add capskills.trait.rafale.visual_channel
scoreboard players set @s CAPSK_RAFALE_VIS 0
execute at @s run playsound minecraft:block.beacon.power_select player @s ~ ~ ~ 0.45 1.55 0
execute at @s run particle minecraft:angry_villager ~ ~1.0 ~ 0.35 0.35 0.35 0.00 16 force @a[distance=..40]
execute at @s anchored eyes rotated as @s positioned ^ ^-0.20 ^1.15 run particle minecraft:angry_villager ~ ~ ~ 0.16 0.16 0.16 0.00 10 force @s
