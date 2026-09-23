function capitale:player/ensure_runtime_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20 run function capitale:dialogue/random/roll_2_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20 if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20 if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Dame Yselle de Verceuil]","color":"yellow"},{"text":" : La littérature a ceci de précieux qu’elle apprend aux gens à entrer dans une pièce avant d’y parler trop fort.","color":"white"}]
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20 if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20 if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Dame Yselle de Verceuil]","color":"yellow"},{"text":" : Les bons livres ne donnent pas seulement des réponses. Ils apprennent à poser les questions sans se couvrir de ridicule.","color":"white"}]
execute if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_DIVERS_LIBRAIRE_NOBLE matches 20 run tellraw @s [{"text":"[Dame Yselle de Verceuil]","color":"yellow"},{"text":" : Vous venez de la librairie agréée ? Approchez, que je voie si l’exemplaire est intact.","color":"white"}]
