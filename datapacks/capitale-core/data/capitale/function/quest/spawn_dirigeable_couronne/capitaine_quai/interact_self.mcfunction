function capitale:player/ensure_runtime_self
execute if score @s QUEST_SPAWN matches 40..99 run function capitale:dialogue/random/roll_2_self
execute if score @s QUEST_SPAWN matches 40..99 if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 40..99 if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : L’amarrage est fait. Géraud Rivet vous attend sur le pont ; il vous indiquera quand quitter le bord.","color":"white"}]
execute if score @s QUEST_SPAWN matches 40..99 if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 40..99 if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Le bâtiment est à quai. Voyez Géraud avant de descendre ; un départ bien ordonné vaut mieux qu’une arrivée confuse.","color":"white"}]
execute if score @s QUEST_SPAWN matches 100.. run function capitale:dialogue/random/roll_3_self
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Le Port vous attend désormais plus que mon pont. Bonne route à terre.","color":"white"}]
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Vous voilà remis aux quais. Que les registres vous pèsent peu, et que la Capitale se montre moins obscure qu’elle n’en a l’air.","color":"white"}]
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_SPAWN matches 100.. if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Nous avons fait notre part en vous ramenant vivant. À vous, désormais, de trouver votre place sous la Couronne.","color":"white"}]
