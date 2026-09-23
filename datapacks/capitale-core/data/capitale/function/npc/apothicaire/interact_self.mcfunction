# Interaction fallback — Apothicaire.
# Le vrai dialogue + bouton boutique doivent être configurés dans EasyNPC.
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Apothicaire]", "color": "dark_green"}, {"text": " : Fioles, poudres, racines et prudence. La dernière manque souvent aux clients.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Apothicaire]", "color": "dark_green"}, {"text": " : Je vends des remèdes simples, pas des miracles. Les miracles ont des formulaires plus longs.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Apothicaire]", "color": "dark_green"}, {"text": " : Les bons mélanges soignent. Les mauvais expliquent pourquoi les étiquettes existent.", "color": "white"}]
