# Interaction fallback — Cartographe.
# Le vrai dialogue + bouton boutique doivent être configurés dans EasyNPC.
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Cartographe]", "color": "blue"}, {"text": " : Cartes, boussoles, repères. Je ne garantis pas que le chemin vous apprécie.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Cartographe]", "color": "blue"}, {"text": " : La Capitale est simple à dessiner de loin. De près, elle ment à l’encre.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Cartographe]", "color": "blue"}, {"text": " : Si vous partez loin, achetez de quoi revenir. C’est souvent négligé.", "color": "white"}]
