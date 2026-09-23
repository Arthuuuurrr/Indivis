function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : Maître Lucain Perrin. Quartier des Vieilles Mécaniques. Près du clocher. Je crains de ne pouvoir rendre l’itinéraire plus simple sans le dessiner.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : Lucain Perrin. Vieilles Mécaniques, non loin du clocher. Si vous vous perdez encore, c’est que vous l’aurez fait avec méthode.","color":"white"}]
