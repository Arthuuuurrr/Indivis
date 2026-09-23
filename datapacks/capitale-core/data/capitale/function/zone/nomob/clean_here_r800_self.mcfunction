function capitale:dialogue/sound/parole_simple_self
tellraw @s [{"text":"[Zone anti-spawn]","color":"dark_green","bold":true},{"text":" Nettoyage manuel des hostiles vanilla non protégés dans un rayon de 800 blocs.","color":"white"}]
execute at @s run function capitale:zone/nomob/clean_marker_r800
