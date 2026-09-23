# 0.9.42 — pulsation de guérison du totem.
scoreboard players set @s CAPSK_CHAMAN_PULSE 0
effect give @a[distance=..7] minecraft:regeneration 3 0 true
# Active les améliorations passives seulement si le propriétaire du totem a les perks.
tag @s remove capskills.chaman.totem.givre_active
tag @s remove capskills.chaman.totem.force_active
tag @s remove capskills.chaman.totem.foudre_active
execute as @a[tag=capskills.chaman.totem.givre.r1] if score @s CAPSK_UID = @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison,limit=1,sort=nearest] CAPSK_CHAMAN_OWNER run tag @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison,limit=1,sort=nearest] add capskills.chaman.totem.givre_active
execute as @a[tag=capskills.chaman.totem.force.r1] if score @s CAPSK_UID = @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison,limit=1,sort=nearest] CAPSK_CHAMAN_OWNER run tag @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison,limit=1,sort=nearest] add capskills.chaman.totem.force_active
execute as @a[tag=capskills.chaman.totem.foudre.r1] if score @s CAPSK_UID = @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison,limit=1,sort=nearest] CAPSK_CHAMAN_OWNER run tag @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison,limit=1,sort=nearest] add capskills.chaman.totem.foudre_active
execute if entity @s[tag=capskills.chaman.totem.givre_active] run function capskills:skill/chaman/totem_givre_pulse_as_totem
execute if entity @s[tag=capskills.chaman.totem.force_active] run function capskills:skill/chaman/totem_force_pulse_as_totem
execute if entity @s[tag=capskills.chaman.totem.foudre_active] run function capskills:skill/chaman/totem_foudre_pulse_as_totem
tag @s remove capskills.chaman.totem.givre_active
tag @s remove capskills.chaman.totem.force_active
tag @s remove capskills.chaman.totem.foudre_active
particle minecraft:heart ~ ~0.8 ~ 0.9 0.25 0.9 0.02 8 force @a[distance=..28]
playsound minecraft:block.amethyst_block.chime player @a[distance=..12] ~ ~ ~ 0.22 1.75 0
