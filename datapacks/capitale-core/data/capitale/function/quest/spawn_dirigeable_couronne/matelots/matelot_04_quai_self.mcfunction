function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Matelot de la Couronne]","color":"yellow"},{"text":" : Le Port vous avalera vite si vous restez sans cap. Écoutez d’abord ceux qui vous orientent, puis choisissez votre route.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Matelot de la Couronne]","color":"yellow"},{"text":" : Ne laissez pas le Port choisir votre route à votre place. Écoutez les bons avertissements, puis avancez.","color":"white"}]
