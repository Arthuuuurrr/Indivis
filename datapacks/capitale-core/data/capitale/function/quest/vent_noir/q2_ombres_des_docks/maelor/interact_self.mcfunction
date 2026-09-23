function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_DOCKS matches 20..40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 20..40 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Pas encore. Revenez avec une piste entière, pas seulement des bribes de peur.","color":"white"}]
execute if score @s QUEST_VN_DOCKS matches 50 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 50 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Les grues de brume... Rask a donc gardé l'ancien refuge. Il pense encore comme mon second.","color":"white"}]
execute if score @s QUEST_VN_DOCKS matches 50 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 50 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Très bien. Nous ne le rejoindrons pas depuis le sol. Il nous faut monter.","color":"white"}]
execute if score @s QUEST_VN_DOCKS matches 50 run function capitale:quest/vent_noir/q2_ombres_des_docks/complete_self
execute if score @s QUEST_VN_DOCKS matches 100 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 100 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : La piste est trouvée. Le prochain pas se fera dans le vent.","color":"white"}]
