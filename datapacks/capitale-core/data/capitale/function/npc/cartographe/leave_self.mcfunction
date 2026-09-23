# Départ — Cartographe
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Cartographe]", "color": "blue"}, {"text": " : Ne confondez pas un trait d’encre avec une route sûre.", "color": "gray"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Cartographe]", "color": "blue"}, {"text": " : Revenez avec des repères, pas seulement des histoires.", "color": "gray"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Cartographe]", "color": "blue"}, {"text": " : Si la carte devient fausse, c’est que vous avez trouvé quelque chose d’intéressant ou de dangereux.", "color": "gray"}]
