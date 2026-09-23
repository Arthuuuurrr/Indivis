# Départ — Marchand d'artefacts
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Marchand d'artefacts]", "color": "dark_purple"}, {"text": " : Gardez vos outils liés à votre rôle. Un artefact oublié finit toujours entre de mauvaises mains.", "color": "gray"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Marchand d'artefacts]", "color": "dark_purple"}, {"text": " : Revenez si vous perdez l’objet, pas si vous perdez le sens de son usage.", "color": "gray"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Marchand d'artefacts]", "color": "dark_purple"}, {"text": " : Que le Cœur reste calme autour de vos mains.", "color": "gray"}]
