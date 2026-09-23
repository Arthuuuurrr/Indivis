# Interaction fallback — Boucher.
# Le vrai dialogue + bouton boutique doivent être configurés dans EasyNPC.
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Boucher]", "color": "dark_red"}, {"text": " : Viande salée, rôtie, fumée. Ce qui tient au corps avant une patrouille ou une traversée.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Boucher]", "color": "dark_red"}, {"text": " : Si vous cherchez du frais, demandez vite. Si vous cherchez du tendre, payez mieux.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Boucher]", "color": "dark_red"}, {"text": " : La lame est propre, le billot aussi. C’est déjà plus que certains quartiers peuvent promettre.", "color": "white"}]
