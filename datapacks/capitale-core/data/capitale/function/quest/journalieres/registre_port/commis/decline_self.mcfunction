function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : Très bien. Le registre attendra, comme il attend toujours ceux qui jurent revenir vite.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : Alors je garde le bordereau sous la pile. C’est là que les urgences prennent racine.","color":"white"}]
