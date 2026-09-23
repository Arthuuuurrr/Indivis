function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_FLAG 0
execute if score @s CAP_FLAG matches 0 if score @s QUEST_DIVERS_LAMPE matches 0 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s QUEST_DIVERS_LAMPE matches 0 run function capitale:quest/divers/lampe_manquante/globe/no_context_self
execute if score @s CAP_FLAG matches 0 if score @s QUEST_DIVERS_LAMPE matches 10 if score @s CAP_GLOBE_L5 matches 0 run scoreboard players set @s CAP_FLAG 2
execute if score @s CAP_FLAG matches 2 run function capitale:quest/divers/lampe_manquante/globe/recalibrate_5_self
execute if score @s CAP_FLAG matches 0 if score @s QUEST_DIVERS_LAMPE matches 10 if score @s CAP_GLOBE_L5 matches 1.. run scoreboard players set @s CAP_FLAG 3
execute if score @s CAP_FLAG matches 3 run function capitale:quest/divers/lampe_manquante/globe/already_this_self
execute if score @s CAP_FLAG matches 0 if score @s QUEST_DIVERS_LAMPE matches 20 run scoreboard players set @s CAP_FLAG 4
execute if score @s CAP_FLAG matches 4 run function capitale:quest/divers/lampe_manquante/globe/all_done_self
execute if score @s CAP_FLAG matches 0 if score @s QUEST_DIVERS_LAMPE matches 30.. run scoreboard players set @s CAP_FLAG 5
execute if score @s CAP_FLAG matches 5 run function capitale:quest/divers/lampe_manquante/globe/postquest_self
