function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Timonier de la Couronne]","color":"yellow"},{"text":" : Tenez-vous bien si vous montez sur le pont. En approche, le bâtiment corrige sa ligne plus souvent qu’on ne le sent depuis les coursives.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Timonier de la Couronne]","color":"yellow"},{"text":" : Sur le pont, la Capitale approche de biais avant de se donner droite. Mieux vaut tenir la rambarde que l’orgueil.","color":"white"}]
