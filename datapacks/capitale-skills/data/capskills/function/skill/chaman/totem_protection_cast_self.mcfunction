# 0.9.40 — @s = chaman. Pose un totem de protection.
execute unless score @s CAPSK_UID matches 1.. run function capskills:integration/assign_uid_self
# Un seul totem chamanique actif par joueur : le nouveau totem remplace l'ancien.
tag @s add capskills.tmp.chaman_caster
execute as @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.protection] if score @s CAPSK_CHAMAN_OWNER = @a[tag=capskills.tmp.chaman_caster,limit=1] CAPSK_UID run function capskills:skill/chaman/totem_protection_remove_as_totem
execute as @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison] if score @s CAPSK_CHAMAN_OWNER = @a[tag=capskills.tmp.chaman_caster,limit=1] CAPSK_UID run function capskills:skill/chaman/totem_guerison_remove_as_totem
summon armor_stand ~ ~0.15 ~ {Tags:["capskills.chaman.totem","capskills.chaman.totem.protection"],NoGravity:1b,Invulnerable:1b,Invisible:1b,Small:1b,CustomNameVisible:0b,ArmorItems:[{},{},{},{id:"minecraft:totem_of_undying",count:1}]}
execute as @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.protection,sort=nearest,limit=1,distance=..2] run scoreboard players operation @s CAPSK_CHAMAN_OWNER = @a[tag=capskills.tmp.chaman_caster,limit=1] CAPSK_UID
scoreboard players set @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.protection,sort=nearest,limit=1,distance=..2] CAPSK_CHAMAN_TOTEM_T 320
scoreboard players set @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.protection,sort=nearest,limit=1,distance=..2] CAPSK_CHAMAN_PULSE 39
scoreboard players set @s CAPSK_CHAMAN_TOTEM_CD 42
particle minecraft:enchanted_hit ~ ~0.8 ~ 0.55 0.35 0.55 0.04 32 force @a[distance=..28]
playsound minecraft:block.beacon.activate player @a[distance=..24] ~ ~ ~ 0.55 1.35 0
title @s actionbar {"text":"Totem de Protection posé : zone défensive 16 s. Recharge : 42 s.","color":"dark_aqua"}
tag @s remove capskills.tmp.chaman_caster
