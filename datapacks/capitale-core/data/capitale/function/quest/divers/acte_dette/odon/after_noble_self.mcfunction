function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Vous avez choisi la voie qui laisse les portes ouvertes. Les gens disent beaucoup de mal de la prudence, puis viennent l’acheter.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Vous avez servi la porte qui s’ouvre plutôt que celle qui claque. Cela se paie rarement mal.","color":"white"}]
