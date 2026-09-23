# Répliques aléatoires — Quincaillier
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Quincaillier]", "color": "gray"}, {"text": " : Les aventuriers achètent des armes. Les survivants achètent des torches.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Quincaillier]", "color": "gray"}, {"text": " : Un bon seau règle plus de problèmes qu’un mauvais discours.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Quincaillier]", "color": "gray"}, {"text": " : Les commandes de chantier montent dès que la Cour décide qu’un couloir mérite un nom.", "color": "white"}]
