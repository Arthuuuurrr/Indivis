execute store result score @s CAP_REWARD_ROLL run random value 1..100
execute if score @s CAP_REWARD_ROLL matches 1..35 run function capitale:rewards/daily_bonus/pool/equipement_base_self
execute if score @s CAP_REWARD_ROLL matches 36..47 run function capitale:rewards/daily_bonus/pool/outils_simples_self
execute if score @s CAP_REWARD_ROLL matches 48..75 run function capitale:rewards/daily_bonus/pool/ressources_utiles_self
execute if score @s CAP_REWARD_ROLL matches 76..93 run function capitale:rewards/daily_bonus/pool/bonus_martins_self
execute if score @s CAP_REWARD_ROLL matches 94..100 run function capitale:rewards/daily_bonus/pool/ressources_rares_self
