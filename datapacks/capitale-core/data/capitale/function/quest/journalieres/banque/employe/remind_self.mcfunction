function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Employé de la Banque]","color":"yellow"},{"text":" : Le pli doit rejoindre la Banque du Cercle. Plus vite il arrive, plus votre course vaut cher.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Employé de la Banque]","color":"yellow"},{"text":" : Gardez le cachet entier. Un document ouvert n’est plus une commission : c’est un ennui.","color":"white"}]
