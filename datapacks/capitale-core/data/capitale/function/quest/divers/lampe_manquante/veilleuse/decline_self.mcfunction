function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Veilleuse des Profondeurs]", "color": "yellow"}, {"text": " Alors la pierre attendra. Elle sait faire, mais elle n’en devient pas plus sûre.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Veilleuse des Profondeurs]", "color": "yellow"}, {"text": " Soit. Les Profondeurs vivent avec les retards, mais elles les font toujours payer à quelqu’un.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Veilleuse des Profondeurs]", "color": "yellow"}, {"text": " Je chercherai une autre main. Les lanternes ne se recalibrent point par souhait.", "color": "white"}]
