function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Libraire agréé]","color":"yellow"},{"text":" : L’exemplaire est scellé et doit rejoindre Dame Yselle de Verceuil, au quartier haut nord. Nul besoin de l’ouvrir : il doit surtout arriver proprement.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Libraire agréé]","color":"yellow"},{"text":" : Vous porterez un livre plus qu’un paquet. Dans les Hauts, la manière de remettre l’objet compte presque autant que l’objet lui-même.","color":"white"}]
function capitale:quest/divers/exemplaire_noble/libraire/show_choices_self
