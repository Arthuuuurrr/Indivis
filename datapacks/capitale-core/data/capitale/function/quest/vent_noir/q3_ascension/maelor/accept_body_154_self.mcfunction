function capitale:quest/dialogue/clear_self
execute if score @s QUEST_VN_DOCKS matches 100 if score @s QUEST_VN_ASCENSION matches 0 if score @s CAP_QUETEACTIVE matches 0 run function capitale:quest/vent_noir/q3_ascension/start_commit_self
execute unless score @s QUEST_VN_DOCKS matches 100 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_VN_DOCKS matches 100 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Il manque encore la piste entière. On ne monte pas vers le ciel sur un simple soupçon.","color":"white"}]
execute if score @s CAP_QUETEACTIVE matches 1.. unless score @s CAP_QUETEACTIVE matches 9 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_QUETEACTIVE matches 1.. unless score @s CAP_QUETEACTIVE matches 9 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Terminez d’abord ce qui vous retient. Un départ manqué coûte plus cher qu’un retard.","color":"white"}]
