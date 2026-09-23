# Répliques aléatoires — Boucher
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Boucher]", "color": "dark_red"}, {"text": " : Les marins veulent du salé, les gardes du consistant, les nobles du présentable.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Boucher]", "color": "dark_red"}, {"text": " : Le vrai luxe, ce n’est pas la viande ; c’est savoir d’où elle vient.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Boucher]", "color": "dark_red"}, {"text": " : Quand une cargaison arrive en retard, tout le marché le sent avant même de le voir.", "color": "white"}]
