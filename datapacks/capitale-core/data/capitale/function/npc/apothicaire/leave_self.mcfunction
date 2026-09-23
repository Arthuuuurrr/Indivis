# Départ — Apothicaire
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Apothicaire]", "color": "dark_green"}, {"text": " : Ne mélangez rien sans lire l’étiquette.", "color": "gray"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Apothicaire]", "color": "dark_green"}, {"text": " : Revenez vivant, c’est meilleur pour les affaires.", "color": "gray"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Apothicaire]", "color": "dark_green"}, {"text": " : Une fiole fermée vaut mieux qu’une expérience improvisée.", "color": "gray"}]
