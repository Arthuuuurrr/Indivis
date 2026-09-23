# 0.9.40 — @s = chaman. Pose un totem de guérison.
execute unless score @s CAPSK_UID matches 1.. run function capskills:integration/assign_uid_self
# Un seul totem chamanique actif par joueur : le nouveau totem remplace l'ancien.
tag @s add capskills.tmp.chaman_caster
execute as @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.protection] if score @s CAPSK_CHAMAN_OWNER = @a[tag=capskills.tmp.chaman_caster,limit=1] CAPSK_UID run function capskills:skill/chaman/totem_protection_remove_as_totem
execute as @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison] if score @s CAPSK_CHAMAN_OWNER = @a[tag=capskills.tmp.chaman_caster,limit=1] CAPSK_UID run function capskills:skill/chaman/totem_guerison_remove_as_totem
summon armor_stand ~ ~0.15 ~ {Tags:["capskills.chaman.totem","capskills.chaman.totem.guerison"],NoGravity:1b,Invulnerable:1b,Invisible:1b,Small:1b,CustomNameVisible:0b,ArmorItems:[{},{},{},{id:"minecraft:glistering_melon_slice",count:1}]}
execute as @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison,sort=nearest,limit=1,distance=..2] run scoreboard players operation @s CAPSK_CHAMAN_OWNER = @a[tag=capskills.tmp.chaman_caster,limit=1] CAPSK_UID
scoreboard players set @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison,sort=nearest,limit=1,distance=..2] CAPSK_CHAMAN_TOTEM_T 280
scoreboard players set @e[type=minecraft:armor_stand,tag=capskills.chaman.totem.guerison,sort=nearest,limit=1,distance=..2] CAPSK_CHAMAN_PULSE 39
scoreboard players set @s CAPSK_CHAMAN_TOTEM_CD 38
particle minecraft:heart ~ ~0.9 ~ 0.55 0.25 0.55 0.02 12 force @a[distance=..28]
playsound minecraft:block.beacon.activate player @a[distance=..24] ~ ~ ~ 0.50 1.55 0
title @s actionbar {"text":"Totem de Guérison posé : soin de maintien 14 s. Recharge : 38 s.","color":"green"}
tag @s remove capskills.tmp.chaman_caster
