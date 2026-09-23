# 1.5.4 alpha 2 — surveillance légère des escortes/guidages.
# Appelée une fois par seconde depuis quest/journalieres/cooldown/second_tick.
# Principe : si le joueur perd son guide pendant 60 secondes, l’escorte est réinitialisée au stade précédent.

function capitale:quest/guidage/sync_self
execute unless score @s CAP_GUIDE_LOCK matches 1.. run scoreboard players set @s CAP_GUIDE_MISS_T 0

execute if score @s CAP_GUIDE_LOCK matches 1.. run scoreboard players set @s CAP_FLAG 0
execute if score @s CAP_GUIDE_LOCK matches 1.. if score @s CAP_GUIDE_ID matches 1 at @s if entity @e[type=armor_stand,tag=guide_leovic_registre_port,distance=..10,limit=1] run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_GUIDE_LOCK matches 1.. if score @s CAP_GUIDE_ID matches 2 at @s if entity @e[type=armor_stand,tag=guide_aurele_coeur,distance=..10,limit=1] run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_GUIDE_LOCK matches 1.. if score @s CAP_GUIDE_ID matches 3 at @s if entity @e[type=armor_stand,tag=guide_roch_profondeurs,distance=..10,limit=1] run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_GUIDE_LOCK matches 1.. if score @s CAP_GUIDE_ID matches 4 at @s if entity @e[type=armor_stand,tag=guide_colin_profondeurs,distance=..10,limit=1] run scoreboard players set @s CAP_FLAG 1

execute if score @s CAP_GUIDE_LOCK matches 1.. if score @s CAP_FLAG matches 1 run scoreboard players set @s CAP_GUIDE_MISS_T 0
execute if score @s CAP_GUIDE_LOCK matches 1.. if score @s CAP_FLAG matches 0 run scoreboard players add @s CAP_GUIDE_MISS_T 1

execute if score @s CAP_GUIDE_LOCK matches 1.. if score @s CAP_GUIDE_MISS_T matches 10 run function capitale:quest/guidage/lost/warn_10_self
execute if score @s CAP_GUIDE_LOCK matches 1.. if score @s CAP_GUIDE_MISS_T matches 30 run function capitale:quest/guidage/lost/warn_30_self
execute if score @s CAP_GUIDE_LOCK matches 1.. if score @s CAP_GUIDE_MISS_T matches 50 run function capitale:quest/guidage/lost/warn_50_self
execute if score @s CAP_GUIDE_LOCK matches 1.. if score @s CAP_GUIDE_MISS_T matches 60.. run function capitale:quest/guidage/lost/timeout_self
