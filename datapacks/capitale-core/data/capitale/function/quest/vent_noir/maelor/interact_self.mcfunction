
function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_CAPITAINE matches 0 if score @s CAP_RANGSOCIAL matches ..19 run function capitale:quest/vent_noir/maelor/locked_not_resident_self
execute if score @s QUEST_VN_CAPITAINE matches 0 if score @s CAP_RANGSOCIAL matches 20.. run function capitale:quest/vent_noir/q1_capitaine_sans_navire/maelor/interact_self
execute if score @s QUEST_VN_CAPITAINE matches 1..30 run function capitale:quest/vent_noir/q1_capitaine_sans_navire/maelor/interact_self
execute if score @s QUEST_VN_CAPITAINE matches 100 if score @s QUEST_VN_DOCKS matches 0 run function capitale:quest/vent_noir/q2_ombres_des_docks/maelor/offer_after_q1_self
execute if score @s QUEST_VN_CAPITAINE matches 100 if score @s QUEST_VN_DOCKS matches 20..50 run function capitale:quest/vent_noir/q2_ombres_des_docks/maelor/interact_self
execute if score @s QUEST_VN_DOCKS matches 100 if score @s QUEST_VN_ASCENSION matches 0 run function capitale:quest/vent_noir/q3_ascension/maelor/offer_after_q2_self
execute if score @s QUEST_VN_ASCENSION matches 100 if score @s QUEST_VN_NAVIRE matches 0 run function capitale:quest/vent_noir/q4_le_vent_noir/maelor/offer_after_ascent_self
execute if score @s QUEST_VN_NAVIRE matches 100 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_NAVIRE matches 100 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Le Vent Noir vole encore. C’est déjà plus que ce que j’espérais.","color":"white"}]
