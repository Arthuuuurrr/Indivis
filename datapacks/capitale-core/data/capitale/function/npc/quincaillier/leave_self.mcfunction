# Départ — Quincaillier
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Quincaillier]", "color": "gray"}, {"text": " : Vérifiez vos torches avant de descendre.", "color": "gray"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Quincaillier]", "color": "gray"}, {"text": " : Revenez quand quelque chose manquera. C’est toujours le cas.", "color": "gray"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Quincaillier]", "color": "gray"}, {"text": " : Que vos outils cassent moins vite que vos plans.", "color": "gray"}]
