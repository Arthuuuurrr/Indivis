
scoreboard players set @s CAP_TEMOIN_QUAI_A 1
function capitale:quest/journalieres/temoignages_quais/count_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Débardeur des Quais]","color":"yellow"},{"text":" : J’ai vu la caisse changer de pile deux fois. Pas portée loin : déplacée juste assez pour que chacun puisse prétendre qu’elle n’était pas de son ressort.","color":"white"}]
execute if score @s CAP_TEMOIGNAGES_COUNT matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_TEMOIGNAGES_COUNT matches 1 run tellraw @s {"text":"[Objectif mis à jour] Témoignages recueillis : 1/3.","color":"yellow"}
execute if score @s CAP_TEMOIGNAGES_COUNT matches 1 run title @s times 5 45 15
execute if score @s CAP_TEMOIGNAGES_COUNT matches 1 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s CAP_TEMOIGNAGES_COUNT matches 1 run title @s subtitle {"text":"Témoignages : 1/3","color":"white"}
execute if score @s CAP_TEMOIGNAGES_COUNT matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_TEMOIGNAGES_COUNT matches 2 run tellraw @s {"text":"[Objectif mis à jour] Deux témoignages recueillis : vous pouvez déjà rendre un rapport, ou poursuivre jusqu’à 3/3.","color":"yellow"}
execute if score @s CAP_TEMOIGNAGES_COUNT matches 2 run title @s times 5 55 15
execute if score @s CAP_TEMOIGNAGES_COUNT matches 2 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s CAP_TEMOIGNAGES_COUNT matches 2 run title @s subtitle {"text":"Rapport possible — 2/3","color":"white"}
execute if score @s CAP_TEMOIGNAGES_COUNT matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_TEMOIGNAGES_COUNT matches 3 run tellraw @s {"text":"[Objectif mis à jour] Les trois témoignages sont réunis. Retournez voir le Greffier des Quais.","color":"yellow"}
execute if score @s CAP_TEMOIGNAGES_COUNT matches 3 run title @s times 5 55 15
execute if score @s CAP_TEMOIGNAGES_COUNT matches 3 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s CAP_TEMOIGNAGES_COUNT matches 3 run title @s subtitle {"text":"Rapport complet — 3/3","color":"white"}
execute at @s run playsound minecraft:item.book.page_turn master @s ~ ~ ~ 216.00 0.95
execute at @s run playsound minecraft:item.book.page_turn master @s ~ ~ ~ 216.00 0.95
scoreboard players set @s CAP_TEMOIGNAGES_COUNT 0
