# Interaction fallback — Marchand d'artefacts.
# Le vrai dialogue + bouton boutique doivent être configurés dans EasyNPC.
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Marchand d'artefacts]", "color": "dark_purple"}, {"text": " : Je ne vends pas la maîtrise. Je remplace seulement les outils de ceux qui l’ont déjà méritée.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Marchand d'artefacts]", "color": "dark_purple"}, {"text": " : Sceaux, relais, focus, alambics : les objets obéissent mal aux mains impatientes.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Marchand d'artefacts]", "color": "dark_purple"}, {"text": " : Un artefact perdu coûte moins cher qu’une doctrine oubliée. Mais il coûte tout de même.", "color": "white"}]
