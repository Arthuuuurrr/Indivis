function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : L’Agent de quai attend toujours la correction. Tant qu’il ne l’a pas confirmée, mon registre reste douteux.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : Sans l’accord de l’Agent de quai, ma correction ne vaut qu’un gribouillage. Allez lui faire valider le bordereau.","color":"white"}]
