function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Matelot de la Couronne]","color":"yellow"},{"text":" : Les canons sont loin derrière nous, mais les ordres de déchargement ne dorment jamais. À peine arrimés, la Couronne recommence à compter.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Matelot de la Couronne]","color":"yellow"},{"text":" : À peine les amarres tendues, les comptes recommencent. La Couronne débarque ses cargaisons aussi vite qu’elle les a scellées.","color":"white"}]
