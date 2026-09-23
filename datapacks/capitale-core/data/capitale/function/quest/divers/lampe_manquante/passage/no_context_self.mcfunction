function capitale:dialogue/random/roll_3_self
function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Passage sombre]","color":"green"},{"text":" : Une ancienne ferrure pend près de la paroi. Quelqu’un entretenait sans doute une lampe ici autrefois.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Passage sombre]","color":"green"},{"text":" : La pierre garde une fraîcheur étrange. Sans consigne précise, mieux vaut ne rien déranger.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Passage sombre]","color":"green"},{"text":" : Les ferrures basses semblent anciennes, mais aucune tâche ne vous appelle ici pour l’instant.","color":"white"}]
