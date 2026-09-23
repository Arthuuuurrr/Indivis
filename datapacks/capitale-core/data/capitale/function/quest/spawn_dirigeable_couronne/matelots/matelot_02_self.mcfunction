function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Matelot de la Couronne]","color":"yellow"},{"text":" : Les soutes étaient lourdes de canons au départ. Nous les avons déposés loin des routes habituelles, sur ordre scellé. Depuis, personne ici ne prétend connaître tout le pourquoi.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Matelot de la Couronne]","color":"yellow"},{"text":" : Nous avons porté des canons là où les cartes se font discrètes. Depuis, chacun à bord se contente de ce qu’il a vu.","color":"white"}]
