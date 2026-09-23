function capitale:dialogue/random/roll_3_self
function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Lanternes du Globe]","color":"green"},{"text":" : Les huit lanternes du socle émettent une lumière stable, presque régulière.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Lanternes du Globe]","color":"green"},{"text":" : Le Globe veille mieux depuis votre recalibrage ; les ombres des Profondeurs semblent moins lourdes.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Lanternes du Globe]","color":"green"},{"text":" : Les flammes basses suivent à nouveau leur cadence. Rien n’est réparé pour toujours, mais le passage respire mieux.","color":"white"}]
