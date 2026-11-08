# PQ02-R1 — Raccord MQ03 : même priorité que le routeur au large, sans changement de stade ou de texte MQ
execute if score @s hcq_mq03 matches 10..30 run return run function hc_quetes:runtime/interact/altheon_brumeforge
function capitale:player/ensure_runtime_self
execute if predicate hc_quetes:migration/state/pq01/40_to_99 run function capitale:dialogue/random/roll_2_self
execute if predicate hc_quetes:migration/state/pq01/40_to_99 if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if predicate hc_quetes:migration/state/pq01/40_to_99 if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : L’amarrage est fait. Géraud Rivet vous attend sur le pont ; il vous indiquera quand quitter le bord.","color":"white"}]
execute if predicate hc_quetes:migration/state/pq01/40_to_99 if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if predicate hc_quetes:migration/state/pq01/40_to_99 if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Le bâtiment est à quai. Voyez Géraud avant de descendre ; un départ bien ordonné vaut mieux qu’une arrivée confuse.","color":"white"}]
execute if predicate hc_quetes:migration/state/pq01/100_to_ run function capitale:dialogue/random/roll_3_self
execute if predicate hc_quetes:migration/state/pq01/100_to_ if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if predicate hc_quetes:migration/state/pq01/100_to_ if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Le Port vous attend désormais plus que mon pont. Bonne route à terre.","color":"white"}]
execute if predicate hc_quetes:migration/state/pq01/100_to_ if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if predicate hc_quetes:migration/state/pq01/100_to_ if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Vous voilà remis aux quais. Que les registres vous pèsent peu, et que la Capitale se montre moins obscure qu’elle n’en a l’air.","color":"white"}]
execute if predicate hc_quetes:migration/state/pq01/100_to_ if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if predicate hc_quetes:migration/state/pq01/100_to_ if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Nous avons fait notre part en vous ramenant vivant. À vous, désormais, de trouver votre place sous la Couronne.","color":"white"}]
