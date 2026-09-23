# Départ — Boucher
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Boucher]", "color": "dark_red"}, {"text": " : Ne laissez pas refroidir ce que vous achetez.", "color": "gray"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Boucher]", "color": "dark_red"}, {"text": " : Revenez quand vous aurez faim plutôt que quand vous serez faible.", "color": "gray"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Boucher]", "color": "dark_red"}, {"text": " : La faim rend les gens stupides. Évitez d’en faire partie.", "color": "gray"}]
