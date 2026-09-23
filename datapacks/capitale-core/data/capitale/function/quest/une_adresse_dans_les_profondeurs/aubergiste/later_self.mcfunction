function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Aubergiste des Profondeurs]","color":"yellow"},{"text":" : Très bien. Les lits se remplissent, mais pas au point de disparaître d’une minute à l’autre.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Aubergiste des Profondeurs]","color":"yellow"},{"text":" : Très bien. Les lits se remplissent, mais pas au point de disparaître d’une minute à l’autre .","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Aubergiste des Profondeurs]","color":"yellow"},{"text":" : Écoutez bien. Très bien. Les lits se remplissent, mais pas au point de disparaître d’une minute à l’autre.","color":"white"}]
