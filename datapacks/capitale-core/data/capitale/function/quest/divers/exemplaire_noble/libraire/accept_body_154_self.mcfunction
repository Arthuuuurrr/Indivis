function capitale:quest/dialogue/clear_self
execute if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 0 run function capitale:quest/divers/exemplaire_noble/libraire/accept_commit_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 0 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 0 run tellraw @s [{"text":"[Libraire agréé]","color":"yellow"},{"text":" : Nous avons déjà parlé de cette remise. Ne mélangeons pas les exemplaires.","color":"white"}]
