function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : Un bordereau de cargaison a été mal reporté. L’Agent de quai saura confirmer la bonne ligne ; sans lui, je ne puis rien clore proprement.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : Il me manque l’accord d’un agent de terrain. Les registres aiment l’encre, mais ils exigent parfois une paire d’yeux sur les quais.","color":"white"}]
function capitale:quest/journalieres/registre_port/commis/show_choices_self
