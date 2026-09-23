function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_GARDEPORT 2
function capitale:dialogue/random/roll_3_self
function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Comme vous voudrez. Sachez seulement que les gens d’ici n’aiment guère les étrangers dont aucun registre ne garde trace. Revenez me voir si vous changez d’avis.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Vous pouvez tarder, mais le Port finit toujours par demander un nom et une ligne d’encre. Je resterai sur les quais.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Très bien. Mais sans registre, chaque passage vous semblera plus étroit qu’il ne devrait.","color":"white"}]
