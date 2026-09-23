function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Libraire agréé]","color":"yellow"},{"text":" : Vous avez déjà prouvé que vous saviez porter un ouvrage sans le perdre. C’est une qualité plus rare qu’on ne croit.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Libraire agréé]","color":"yellow"},{"text":" : Un ouvrage confié, un ouvrage remis : vous avez montré plus de soin que bien des clients très bien nés.","color":"white"}]
