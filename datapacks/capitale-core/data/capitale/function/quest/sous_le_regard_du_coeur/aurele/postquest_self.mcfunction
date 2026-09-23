function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Aurèle Veyrane]", "color": "yellow"}, {"text": " Les voies du Cœur vous sont désormais moins étrangères. Souvenez-vous : il ne crée pas les plans, il tient les distances qui les séparent.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Aurèle Veyrane]", "color": "yellow"}, {"text": " Vous avez vu ce que la Couronne laisse voir des voies du Cœur. Marchez sans empressement, et vous éviterez bien des questions.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Aurèle Veyrane]", "color": "yellow"}, {"text": " Le Cœur règle les portails plus qu’il ne les ouvre. C’est pourquoi la garde préfère un pas lent à une curiosité vive.", "color": "white"}]
