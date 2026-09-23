function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_CAPITAINE matches 0 run function capitale:quest/vent_noir/q1_capitaine_sans_navire/maelor/offer_self
execute if score @s QUEST_VN_CAPITAINE matches 1..2 run function capitale:quest/vent_noir/q1_capitaine_sans_navire/maelor/open_choices_self
execute if score @s QUEST_VN_CAPITAINE matches 20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_CAPITAINE matches 20 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Ils ne quitteront pas cette venelle tant qu'ils me croiront seul. Faites-les reculer, et nous parlerons du Vent Noir.","color":"white"}]
execute if score @s QUEST_VN_CAPITAINE matches 30 run function capitale:quest/vent_noir/q1_capitaine_sans_navire/maelor/return_after_brawl_self
execute if score @s QUEST_VN_CAPITAINE matches 100 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_CAPITAINE matches 100 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Nous avons déjà laissé cette première mauvaise affaire derrière nous.","color":"white"}]
