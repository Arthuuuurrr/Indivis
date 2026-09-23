function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_FLAG 0
execute if score @s CAP_QUETEACTIVE matches 0 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QUETEACTIVE matches 6 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 run function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/accept_commit_self
execute unless score @s CAP_FLAG matches 1 run function capitale:quest/blocked_active_self
execute unless score @s CAP_FLAG matches 1 run function capitale:quest/dialogue/clear_self
