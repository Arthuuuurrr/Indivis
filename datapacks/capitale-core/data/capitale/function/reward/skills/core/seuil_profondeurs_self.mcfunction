scoreboard players add @s CAPSK_REWARD_PROF 0
execute if score @s CAPSK_REWARD_PROF matches 0 run function capskills:reward/core/seuil_profondeurs_self
execute if score @s CAPSK_REWARD_PROF matches 0 run function capitale:reward/xp_vanilla/core/seuil_profondeurs_self
execute if score @s CAPSK_REWARD_PROF matches 0 run scoreboard players set @s CAPSK_REWARD_PROF 1
