function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SPAWN 0
execute if score @s QUEST_SPAWN matches 0..9 run function capitale:quest/spawn_dirigeable_couronne/geraud_large_start_self
execute if score @s QUEST_SPAWN matches 10..39 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 10..39 run tellraw @s [{"text":"[Quartier-maître Géraud Rivet]","color":"yellow"},{"text":" : Le capitaine Brumeforge vous attend dans sa cabine. Présentez-vous à lui avant l’accostage.","color":"white"}]
execute if score @s QUEST_SPAWN matches 40.. run function capitale:quest/spawn_dirigeable_couronne/geraud_quai_interact_self
