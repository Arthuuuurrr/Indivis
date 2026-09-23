function capitale:player/ensure_runtime_self
execute if score @s QUEST_SPAWN matches 0 if score @s CAP_QUETEACTIVE matches 0 run function capitale:quest/spawn_dirigeable_couronne/geraud_large_start_self
