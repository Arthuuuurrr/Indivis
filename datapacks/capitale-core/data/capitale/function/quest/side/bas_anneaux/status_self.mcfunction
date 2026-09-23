function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SIDE_BA_Q01 0
scoreboard players add @s QUEST_SIDE_BA_Q02 0
scoreboard players add @s QUEST_SIDE_BA_Q03 0
scoreboard players add @s QUEST_SIDE_BA_Q04 0
scoreboard players add @s REP_BAS_ANNEAUX 0
scoreboard players add @s REP_BA_GARDES 0
scoreboard players add @s REP_BA_ARTISANS 0
scoreboard players add @s REP_BA_MONDE_GRIS 0
tellraw @s [{"text":"[Bas-Anneaux — secondaires]","color":"gold"},{"text":" Q01=","color":"white"},{"score":{"name":"@s","objective":"QUEST_SIDE_BA_Q01"},"color":"yellow"},{"text":" Q02=","color":"white"},{"score":{"name":"@s","objective":"QUEST_SIDE_BA_Q02"},"color":"yellow"},{"text":" Q03=","color":"white"},{"score":{"name":"@s","objective":"QUEST_SIDE_BA_Q03"},"color":"yellow"},{"text":" Q04=","color":"white"},{"score":{"name":"@s","objective":"QUEST_SIDE_BA_Q04"},"color":"yellow"}]
tellraw @s [{"text":"[Réputations BA]","color":"gold"},{"text":" habitants=","color":"white"},{"score":{"name":"@s","objective":"REP_BAS_ANNEAUX"},"color":"yellow"},{"text":" gardes=","color":"white"},{"score":{"name":"@s","objective":"REP_BA_GARDES"},"color":"yellow"},{"text":" artisans=","color":"white"},{"score":{"name":"@s","objective":"REP_BA_ARTISANS"},"color":"yellow"},{"text":" gris=","color":"white"},{"score":{"name":"@s","objective":"REP_BA_MONDE_GRIS"},"color":"yellow"}]
