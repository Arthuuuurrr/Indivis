# Départ — Agriculteur
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Agriculteur]", "color": "green"}, {"text": " : Revenez avant que les paniers soient vides.", "color": "gray"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Agriculteur]", "color": "green"}, {"text": " : Que la route vous laisse les bottes sèches.", "color": "gray"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Agriculteur]", "color": "green"}, {"text": " : Gardez un peu de pain sur vous. La Capitale fatigue vite les imprévoyants.", "color": "gray"}]
