function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SPAWN 0
execute if score @s QUEST_SPAWN matches 0..9 run function capitale:quest/spawn_dirigeable_couronne/geraud_large_near_self
execute if score @s QUEST_SPAWN matches 40..99 run function capitale:quest/spawn_dirigeable_couronne/geraud_quai_near_self
