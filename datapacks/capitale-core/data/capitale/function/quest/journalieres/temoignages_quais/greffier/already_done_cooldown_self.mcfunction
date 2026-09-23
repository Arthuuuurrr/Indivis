function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Le dernier dossier est encore chaud dans mon registre. Revenez après la prochaine rotation de vingt heures.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Je n’ouvre pas deux dossiers de suite pour le même désordre. Revenez après vingt heures.","color":"white"}]
