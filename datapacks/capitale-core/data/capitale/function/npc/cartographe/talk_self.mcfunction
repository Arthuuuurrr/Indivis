# Répliques aléatoires — Cartographe
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Cartographe]", "color": "blue"}, {"text": " : Les routes changent moins vite que les rumeurs, mais elles tuent plus sûrement les distraits.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Cartographe]", "color": "blue"}, {"text": " : Les nefs donnent l’impression de dominer le monde. Les cartes rappellent qu’il reste grand.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Cartographe]", "color": "blue"}, {"text": " : Certains passages ne sont pas absents des cartes. Ils sont seulement absents des cartes publiques.", "color": "white"}]
