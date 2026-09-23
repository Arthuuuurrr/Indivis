scoreboard players set @s QUEST_DIVERS_RELAIS_COEUR 1
function capitale:dialogue/random/roll_6_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : J’ai besoin d’une inspection courte aux relais du Cœur. Trois bornes, trois réponses à relever, et pas de grande théorie sur les plans.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Les Ingénieurs parlent de charge et de portails ; moi, je veux surtout savoir si trois relais répondent encore juste. Pouvez-vous vous en charger ?","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Le Cœur ne dort point. Quand ses relais changent de ton, quelqu’un doit aller écouter avant que la garde ne s’inquiète.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 4 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 4 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Trois relais doivent être contrôlés dans les galeries intérieures. Ce n’est pas glorieux, mais c’est le genre de détail qui évite les rapports funestes.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 5 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 5 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : J’ai un laissez-passer court et trop peu de mains disponibles. Si vous savez suivre des repères sans toucher à tout, cela peut suffire.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 6 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 6 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Les relais ne règlent pas le Cœur ; ils signalent ses réponses locales. Quand le signal se trouble, je préfère vérifier avant que cela devienne une affaire de garde.","color":"white"}]

function capitale:quest/divers/relais_coeur/technicien/open_choices_self
