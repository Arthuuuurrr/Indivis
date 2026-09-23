# Interaction fallback — Agriculteur.
# Le vrai dialogue + bouton boutique doivent être configurés dans EasyNPC.
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Agriculteur]", "color": "green"}, {"text": " : Les étals sont simples, mais les greniers tiennent la ville debout.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Agriculteur]", "color": "green"}, {"text": " : Semences, bottes, légumes : rien de glorieux, mais tout le monde finit par revenir en acheter.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Agriculteur]", "color": "green"}, {"text": " : Le Port brasse du bruit, les Archives gardent les mots ; moi, je garde les ventres remplis.", "color": "white"}]
