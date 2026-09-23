function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Veilleuse des Profondeurs]","color":"yellow"},{"text":" : Le Globe attend encore votre main. Huit lanternes, pas une de moins ; le socle ne tient son rythme que si chacune répond aux autres.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Veilleuse des Profondeurs]","color":"yellow"},{"text":" : Cherchez les lanternes du socle. Leur lumière doit reprendre ensemble, comme une ronde bien réglée.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Veilleuse des Profondeurs]","color":"yellow"},{"text":" : Les gens passent sous le Globe sans le regarder. Pourtant, quand ses lanternes mentent, ce sont les pas d’en bas qui se perdent.","color":"white"}]
tellraw @s [{"text":"[Progression] ","color":"gray"},{"score":{"name":"@s","objective":"CAP_GLOBE_DONE"},"color":"yellow"},{"text":"/8 lanternes recalibrées.","color":"gray"}]
