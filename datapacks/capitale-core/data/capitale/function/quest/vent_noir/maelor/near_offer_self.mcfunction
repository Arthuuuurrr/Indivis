# Hotfix 14 near cooldown : anti-spam joueur, 120 ticks
function capitale:player/ensure_runtime_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_VN_CAPITAINE matches 0 if score @s CAP_QUETEACTIVE matches 0 if score @s CAP_RANGSOCIAL matches ..19 run function capitale:quest/vent_noir/maelor/near_locked_not_resident_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s QUEST_VN_CAPITAINE matches 0 if score @s CAP_QUETEACTIVE matches 0 if score @s CAP_RANGSOCIAL matches 20.. run function capitale:quest/vent_noir/q1_capitaine_sans_navire/maelor/near_offer_self
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s NPC_NEAR_CD 120
