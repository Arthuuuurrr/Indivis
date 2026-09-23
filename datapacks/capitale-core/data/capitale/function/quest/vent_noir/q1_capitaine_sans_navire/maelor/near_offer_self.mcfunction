function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_CAPITAINE matches 0 if score @s CAP_QUETEACTIVE matches 0 run function capitale:quest/vent_noir/q1_capitaine_sans_navire/maelor/offer_self
