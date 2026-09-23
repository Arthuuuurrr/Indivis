function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_DISC_BANQUE 1
execute if score @s QUEST_DAILY_BANQUE matches 10 run function capitale:quest/journalieres/banque/banquier/attempt_turn_in_self
execute unless score @s QUEST_DAILY_BANQUE matches 10 run function capitale:quest/journalieres/banque/banquier/ambient_self
