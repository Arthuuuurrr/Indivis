function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_FLAG 0
execute if score @s CAP_QUETEACTIVE matches 0 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QUETEACTIVE matches 4 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 run function capitale:quest/le_registre_du_port/leovic/accept_commit_self
execute unless score @s CAP_FLAG matches 1 run function capitale:quest/blocked_active_self
execute unless score @s CAP_FLAG matches 1 run function capitale:quest/dialogue/clear_self
