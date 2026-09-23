function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Les registres resteront ouverts. Revenez avec la somme et je clorai votre inscription.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Les registres resteront ouverts. Repassez avec la somme et je clorai votre inscription.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Écoutez bien. Les registres resteront ouverts. Revenez avec la somme et je clorai votre inscription.","color":"white"}]
