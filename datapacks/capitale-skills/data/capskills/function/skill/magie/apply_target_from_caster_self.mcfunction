# @s = lanceur joueur. Cible = entite taggee capskills.magic_target. 0.8.11 : cooldown 4 s, 3 s avec Canalisation rapide.
# 0.7.6 : équilibrage global conservé ; recul par tp retiré pour éviter les comportements instables.
execute unless entity @e[tag=capskills.magic_target,limit=1,sort=nearest] run title @s actionbar {"text":"Cible magique perdue avant impact.","color":"red"}
execute if entity @e[tag=capskills.magic_target,limit=1,sort=nearest] at @s run playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.65 1.45 0
execute at @e[tag=capskills.magic_target,limit=1,sort=nearest] run particle minecraft:end_rod ~ ~1.0 ~ 0.55 0.70 0.55 0.05 35 force @a[distance=..32]
execute at @e[tag=capskills.magic_target,limit=1,sort=nearest] run particle minecraft:witch ~ ~1.0 ~ 0.95 1.10 0.95 0.11 180 force @a[distance=..32]
execute at @e[tag=capskills.magic_target,limit=1,sort=nearest] run particle minecraft:dragon_breath ~ ~1.0 ~ 0.85 1.00 0.85 0.07 90 force @a[distance=..32]
execute at @e[tag=capskills.magic_target,limit=1,sort=nearest] run particle minecraft:damage_indicator ~ ~1.0 ~ 0.50 0.60 0.50 0.09 28 force @a[distance=..32]
execute at @e[tag=capskills.magic_target,limit=1,sort=nearest] run playsound minecraft:entity.evoker.cast_spell player @a[distance=..32] ~ ~ ~ 1.05 1.18 0

# Dégâts actifs 0.7.6.
# Focus I : 8 ; Focus II : 16 ; Focus III : 24.
# Décharge I ajoute +8 ; Décharge II ajoute +12.
# Total typique : Focus II + Décharge I = 24. Max DPS magie = 36 par impact, recharge 4 s.
execute if score @s CAPSK_MAG_RANK matches 1 run damage @e[tag=capskills.magic_target,limit=1,sort=nearest] 8 minecraft:generic by @s
execute if score @s CAPSK_MAG_RANK matches 2 run damage @e[tag=capskills.magic_target,limit=1,sort=nearest] 16 minecraft:generic by @s
execute if score @s CAPSK_MAG_RANK matches 3.. run damage @e[tag=capskills.magic_target,limit=1,sort=nearest] 24 minecraft:generic by @s
execute if score @s CAPSK_MAG_DPS_RANK matches 1 run damage @e[tag=capskills.magic_target,limit=1,sort=nearest] 8 minecraft:generic by @s
execute if score @s CAPSK_MAG_DPS_RANK matches 2.. run damage @e[tag=capskills.magic_target,limit=1,sort=nearest] 12 minecraft:generic by @s

# Recul magique désactivé en 0.7.6 : pas de pseudo-knockback par tp, jugé trop instable/buggable.
# Les dégâts, sons, particules et effets de spécialisation restent actifs.

# Effets de specialisation non-DPS.
execute if entity @s[tag=capskills.magie.concentration.r1] run effect give @e[tag=capskills.magic_target,limit=1,sort=nearest] minecraft:glowing 4 0 true
execute if entity @s[tag=capskills.magie.surcharge.r1] if score @s CAPSK_MAG_DPS_RANK matches 1.. run damage @e[tag=capskills.magic_target,limit=1,sort=nearest] 2 minecraft:generic by @s
execute if score @s CAPSK_MAG_RES_RANK matches 1.. run effect give @e[tag=capskills.magic_target,limit=1,sort=nearest] minecraft:glowing 5 0 true
scoreboard players set @s CAPSK_MAGIE_CD 4
execute if entity @s[tag=capskills.magie.quick.r1] run scoreboard players set @s CAPSK_MAGIE_CD 3
execute unless entity @s[tag=capskills.magie.quick.r1] run title @s actionbar {"text":"Décharge destructrice projetée. Recharge : 4 s.","color":"light_purple"}
execute if entity @s[tag=capskills.magie.quick.r1] run title @s actionbar {"text":"Décharge destructrice rapide projetée. Recharge : 3 s.","color":"light_purple"}
execute as @e[tag=capskills.magic_target,type=minecraft:player,limit=1,sort=nearest] run title @s actionbar {"text":"Vous êtes frappé par une décharge du Cœur.","color":"dark_purple"}
