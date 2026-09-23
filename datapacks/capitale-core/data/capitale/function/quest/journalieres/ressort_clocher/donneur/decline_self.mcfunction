function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Commis des Messageries]","color":"yellow"},{"text":" : Très bien. Le ressort attendra un autre porteur, si le clocher consent à attendre autant.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Commis des Messageries]","color":"yellow"},{"text":" : Alors je garde la pièce sous scellé. Les horlogères détestent les retards, mais moins que les pièces perdues.","color":"white"}]
