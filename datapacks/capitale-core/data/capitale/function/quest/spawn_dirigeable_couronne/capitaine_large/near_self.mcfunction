function capitale:player/ensure_runtime_self
execute if score @s QUEST_SPAWN matches 10..19 run function capitale:quest/spawn_dirigeable_couronne/capitaine_large/near_intro_self
