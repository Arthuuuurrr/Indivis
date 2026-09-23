function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Libraire agréé]","color":"yellow"},{"text":" : Dommage. Un livre remis à temps gagne parfois plus de portes qu’une recommandation bavarde.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Libraire agréé]","color":"yellow"},{"text":" : Très bien. Les livres savent attendre ; les maisons nobles, beaucoup moins.","color":"white"}]
