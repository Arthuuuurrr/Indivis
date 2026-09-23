function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Il me faut au moins deux témoignages. Le Débardeur, la Marinière et le Clerc des Bordereaux n’iront pas très loin.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Deux dépositions me suffiront au moins. Les trois témoins demeurent près des quais ; faites-les parler.","color":"white"}]
