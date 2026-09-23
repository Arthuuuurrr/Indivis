# Répliques aléatoires — Agriculteur
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Agriculteur]", "color": "green"}, {"text": " : Les récoltes proches de la Capitale ne suffisent jamais. Il faut des terres, des bras et de la patience.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Agriculteur]", "color": "green"}, {"text": " : Une mauvaise saison se voit d’abord dans les tavernes : moins de pain, plus de disputes.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Agriculteur]", "color": "green"}, {"text": " : Les guildes promettent beaucoup, mais quand les greniers baissent, elles viennent toutes négocier.", "color": "white"}]
