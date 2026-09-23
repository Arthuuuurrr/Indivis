scoreboard players set @s QUEST_VN_CAPITAINE 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Vous avez l'air d'avoir encore le goût des ennuis. Le mien s'appelle Vent Noir, et on me l'a pris.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Avant de parler d'un navire, il faudrait déjà convaincre les chiens de Rask que je ne suis pas sans défense.","color":"white"}]
function capitale:quest/vent_noir/q1_capitaine_sans_navire/maelor/open_choices_self
