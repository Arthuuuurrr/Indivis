
scoreboard players set @s QUEST_DIVERS_ACTE_DETTE 30
function capitale:quest/divers/acte_dette/document/give_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Cette dette n’est pas la mienne. Enfin… pas ainsi. J’ai emprunté, oui. J’ai signé un acte, oui. Mais pas ces montants-là, pas ces intérêts-là, pas ces lignes ajoutées après coup.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Mon atelier n’est pas un palais, mais il nourrit deux apprentis, répare les mécanismes du quartier et tient la maison debout. Avec ce papier falsifié, tout peut être saisi proprement, légalement, froidement.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Prenez l’acte. Odon voudra vous expliquer que c’est plus compliqué. Peut-être. Mais parfois les complications servent seulement à cacher une main qui réécrit les chiffres.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Retournez voir Odon Varenne au Port avec l’acte falsifié.","color":"yellow"}
title @s times 5 55 15
title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
title @s subtitle {"text":"Retournez voir Odon Varenne.","color":"white"}
execute at @s run playsound minecraft:item.book.page_turn master @s ~ ~ ~ 216.00 0.95
execute at @s run playsound minecraft:item.book.page_turn master @s ~ ~ ~ 216.00 0.95
