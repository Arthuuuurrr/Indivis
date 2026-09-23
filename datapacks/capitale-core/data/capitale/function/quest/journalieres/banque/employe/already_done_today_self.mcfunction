function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Employé de la Banque]", "color": "yellow"}, {"text": " Les plis du jour sont clos. Revenez lorsque la Banque rouvrira ses écritures.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Employé de la Banque]", "color": "yellow"}, {"text": " Pour aujourd’hui, la Banque n’a plus rien à faire porter. Les registres respirent enfin un peu.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Employé de la Banque]", "color": "yellow"}, {"text": " Les courses de ce jour sont inscrites. La Banque ne double pas ses plis par caprice.", "color": "white"}]
