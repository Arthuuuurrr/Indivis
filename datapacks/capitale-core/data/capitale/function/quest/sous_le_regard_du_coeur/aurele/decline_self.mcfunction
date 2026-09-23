function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_3_self
function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Faites à votre guise. Restez seulement sur les voies permises : hors de ces axes, les gardes vous reconduiront.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Alors regardez par vous-même, mais souvenez-vous : près du Cœur, les détours ne pardonnent guère aux nouveaux venus.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Je ne vous retiens pas. La Capitale laisse marcher les curieux, mais elle note toujours où ils posent les pieds.","color":"white"}]
