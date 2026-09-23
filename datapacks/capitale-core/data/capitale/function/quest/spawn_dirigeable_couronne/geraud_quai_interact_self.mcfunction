function capitale:player/ensure_runtime_self
execute if score @s QUEST_SPAWN matches 40..99 if score @s CAP_QSEQ matches 0 run function capitale:quest/spawn_dirigeable_couronne/geraud_quai_complete_self
execute if score @s QUEST_SPAWN matches 40..99 unless score @s CAP_QSEQ matches 0 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 40..99 unless score @s CAP_QSEQ matches 0 run tellraw @s [{"text":"[Quartier-maître Géraud Rivet]","color":"yellow"},{"text":" : Un instant. Laissez-moi terminer.","color":"white"}]
execute if score @s QUEST_SPAWN matches 100.. run function capitale:dialogue/random/roll_3_self
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Quartier-maître Géraud Rivet]","color":"yellow"},{"text":" : La passerelle reste libre. Le Port est devant vous ; bonne route à terre.","color":"white"}]
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Quartier-maître Géraud Rivet]","color":"yellow"},{"text":" : Vous avez quitté le bord comme il fallait. À présent, suivez les quais et gardez l’œil sur les registres.","color":"white"}]
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Quartier-maître Géraud Rivet]","color":"yellow"},{"text":" : Si le Port vous paraît bruyant, dites-vous qu’il l’est encore moins que les bureaux lorsqu’un nom manque aux registres.","color":"white"}]
