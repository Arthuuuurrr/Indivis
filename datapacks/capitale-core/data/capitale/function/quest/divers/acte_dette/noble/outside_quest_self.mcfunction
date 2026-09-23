function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Dame Éléonore de Vaudrec]","color":"yellow"},{"text":" : Je ne reçois pas les curieux. Les dettes, les héritages et les silences de famille ne se commentent pas sans raison valable.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Dame Éléonore de Vaudrec]","color":"yellow"},{"text":" : Je n’accorde pas d’audience aux passants. Lorsqu’une affaire vous concernera, on vous en fera part.","color":"white"}]
