# Répliques aléatoires — Apothicaire
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Apothicaire]", "color": "dark_green"}, {"text": " : Les Profondeurs fournissent des choses utiles, mais rarement rassurantes.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Apothicaire]", "color": "dark_green"}, {"text": " : N’achetez pas une fiole parce qu’elle brille. C’est rarement le bon critère.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Apothicaire]", "color": "dark_green"}, {"text": " : Les gardes aiment les potions qui tiennent debout ; les nobles, celles qui ne se voient pas.", "color": "white"}]
