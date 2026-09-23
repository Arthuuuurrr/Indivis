function capitale:quest/dialogue/clear_self
execute if score @s QUEST_VN_ASCENSION matches 100 if score @s QUEST_VN_NAVIRE matches 0 if score @s CAP_QUETEACTIVE matches 0 run function capitale:quest/vent_noir/q4_le_vent_noir/start_commit_self
execute unless score @s QUEST_VN_ASCENSION matches 100 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_VN_ASCENSION matches 100 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Pas avant que la montée soit achevée. On n’aborde pas un navire qu’on n’a pas encore rejoint.","color":"white"}]
execute if score @s CAP_QUETEACTIVE matches 1.. unless score @s CAP_QUETEACTIVE matches 10 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_QUETEACTIVE matches 1.. unless score @s CAP_QUETEACTIVE matches 10 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Vous êtes déjà pris ailleurs. Reprenez vos esprits avant de choisir un pont ennemi.","color":"white"}]
