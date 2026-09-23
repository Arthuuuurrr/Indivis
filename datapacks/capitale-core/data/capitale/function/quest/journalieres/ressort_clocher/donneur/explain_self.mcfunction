function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Commis des Messageries]","color":"yellow"},{"text":" : Le ressort doit rejoindre l’Horlogère du Clocher, dans le Quartier des Vieilles Mécaniques. C’est une petite pièce, mais un retard suffit à dérégler plus grand qu’elle.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Commis des Messageries]","color":"yellow"},{"text":" : Portez cette pièce aux Vieilles Mécaniques. Les mécanismes de clocher ont l’air patients, mais leur patience finit toujours par coûter cher.","color":"white"}]
function capitale:quest/journalieres/ressort_clocher/donneur/show_choices_self
