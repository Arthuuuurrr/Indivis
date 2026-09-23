function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_3_self
function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : À votre guise. Les registres ne s’impatientent pas ; ils attendent simplement que les gens finissent par avoir besoin d’eux.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Les Profondeurs accueillent mal les gens sans adresse. Revenez quand vous voudrez cesser d’être seulement de passage.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Fort bien. Les guichets ferment plus vite que les besoins ; vous reviendrez peut-être avec de meilleures raisons.","color":"white"}]
