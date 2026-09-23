function capitale:player/ensure_runtime_self
execute if score @s QUEST_SPAWN matches 10..39 run function capitale:quest/spawn_dirigeable_couronne/capitaine_large/ouvrir_conversation_self
execute if score @s QUEST_SPAWN matches 40..99 run function capitale:dialogue/random/roll_2_self
execute if score @s QUEST_SPAWN matches 40..99 if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 40..99 if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : L’approche est derrière nous. Géraud vous attend sur le pont ; il vous indiquera la suite.","color":"white"}]
execute if score @s QUEST_SPAWN matches 40..99 if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 40..99 if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Nous sommes presque remis au Port. Voyez le quartier-maître ; il connaît mieux que moi l’ordre des passerelles.","color":"white"}]
execute if score @s QUEST_SPAWN matches 100.. run function capitale:dialogue/random/roll_3_self
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Bonne route à vous. Que la Haute Capitale se montre plus lisible depuis ses quais qu’elle ne l’était depuis le ciel.","color":"white"}]
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Vous avez quitté mon bord ; gardez seulement ceci : dans cette ville, mieux vaut entendre les registres avant les chaînes.","color":"white"}]
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Si vous cherchez encore votre route, commencez par les quais. Toute la Capitale finit par y laisser une trace.","color":"white"}]
