function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_ALDREN_ORCS 0
playsound minecraft:entity.villager.ambient neutral @s ~ ~ ~ 1.0 1.0
execute if score @s QUEST_ALDREN_ORCS matches 0 run function capitale:quest/side/menace_orcs/offer_self
execute if score @s QUEST_ALDREN_ORCS matches 10 run function capitale:quest/side/menace_orcs/progress_self
execute if score @s QUEST_ALDREN_ORCS matches 20 run function capitale:quest/side/menace_orcs/report_self
execute if score @s QUEST_ALDREN_ORCS matches 30 run function capitale:quest/side/menace_orcs/done_self
