scoreboard players set @s QUEST_DIVERS_LIBRAIRE_NOBLE 20
scoreboard players set @s CAP_LIBRAIRE_NOBLE_PASS 0
execute if score @s ACCESS_QH_NORD matches 0 run scoreboard players set @s CAP_LIBRAIRE_NOBLE_PASS 1
execute if score @s ACCESS_QH_NORD matches 0 run function capitale:access/grant/quartiers_hauts_nord_self
give @s written_book[written_book_content={pages:[[[{"text":"Exemplaire scellé","color":"dark_purple"},"\n\n",{"text":"À remettre intact au destinataire désigné par le libraire agréé. Toute ouverture non sollicitée serait d’un goût discutable.","color":"black"}]]],title:"Book Title",author:"Librairie agréée de la Capitale",generation:2},minecraft:custom_model_data={strings:['quest_libraire_exemplaire_noble']},custom_name=[{"text":"Exemplaire scellé à remettre","italic":false,"color":"light_purple"}],lore=[[{"text":"Commission du libraire agréé","italic":false,"color":"gray"}],[{"text":"Destiné au quartier noble nord","italic":false,"color":"dark_gray"}]]] 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Libraire agréé]","color":"yellow"},{"text":" : Voici l’exemplaire. Le destinataire se nomme","color":"white"},{"text":"Dame Yselle de Verceuil","color":"light_purple"},{"text":". Sa demeure se trouve dans le quartier haut nord.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Nouvelle quête Divers — Un exemplaire à remettre.","color":"gold"}
title @s times 5 55 15
title @s title {"text":"Nouvelle quête","color":"gold","bold":true}
title @s subtitle {"text":"Un exemplaire à remettre","color":"white"}
title @s times 5 50 15
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Remettez l’exemplaire à Dame Yselle de Verceuil, dans le quartier noble nord.","color":"yellow"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.25
