function capitale:dialogue/random/roll_3_self
function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Veilleuse des Profondeurs]", "color": "yellow"}, {"text": " Le Globe tient mieux depuis votre passage. Revenez après la prochaine rotation des lanternes.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Veilleuse des Profondeurs]", "color": "yellow"}, {"text": " Les lanternes du socle répondent de nouveau ensemble. Les Profondeurs aiment les gestes précis plus que les grandes promesses.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Veilleuse des Profondeurs]", "color": "yellow"}, {"text": " Les Profondeurs ne remercient pas toujours à voix haute. Mais elles retiennent les mains qui ont remis la lumière en ordre.", "color": "white"}]
