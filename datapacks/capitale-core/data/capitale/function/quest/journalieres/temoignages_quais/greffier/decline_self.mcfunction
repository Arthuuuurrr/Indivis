function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Alors les dépositions attendront. Les témoins, eux, risquent de se souvenir autrement demain.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Je note votre refus de service. Enfin, non : je manque déjà de papier pour les refus utiles.","color":"white"}]
