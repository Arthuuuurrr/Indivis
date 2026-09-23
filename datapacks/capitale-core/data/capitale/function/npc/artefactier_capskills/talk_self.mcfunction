# Répliques aléatoires — Marchand d'artefacts
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Marchand d'artefacts]", "color": "dark_purple"}, {"text": " : Ces pièces ne devraient pas être collectionnées. Elles devraient être comprises.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Marchand d'artefacts]", "color": "dark_purple"}, {"text": " : Le Cœur laisse des traces dans les outils qui servent près de lui trop longtemps.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Marchand d'artefacts]", "color": "dark_purple"}, {"text": " : Acheter l’objet ne donne pas le savoir. Les compétences, elles, restent dans votre doctrine.", "color": "white"}]
