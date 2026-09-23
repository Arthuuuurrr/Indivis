function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_6_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Cherchez trois bornes au noyau violet. Le cuivre autour d’elles est noirci, mais le rythme doit rester net et régulier.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Les relais ne commandent pas le Cœur ; ils lisent ses réponses locales. Si l’un se dérègle, les passages autour deviennent moins sûrs.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Le Cœur règle les distances entre les plans et stabilise certains portails. Ces relais n’en sont que des témoins, mais un témoin faussé suffit à troubler une salle entière.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 4 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 4 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Ne forcez rien. Vous observez, vous confirmez la réponse, puis vous revenez. Les réparations appartiennent aux Ingénieurs du Cœur.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 5 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 5 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Un bon relais répond sans à-coup. S’il vibre trop fort, s’il reste muet, ou si la lumière décroche, je veux le savoir.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 6 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 6 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Le mot seuil existe dans les traités anciens. Pour ce travail, pensez plus simplement : portails, distances, réponses locales.","color":"white"}]

function capitale:quest/divers/relais_coeur/technicien/open_choices_self
