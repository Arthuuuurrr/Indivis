function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SIDE_BA_Q01 0
scoreboard players add @s QUEST_SIDE_BA_Q03 0
# Q01 se déclenche maintenant par proximité : vol / bousculade / fuite.
function capitale:quest/side/bas_anneaux/q01_quartier_oublies/near_trigger_self
# Si la scène est en cours, cette fonction aide à lancer ou débloquer le guide de Mira.
function capitale:npc/mira/path/q01/near_self
execute if score @s QUEST_SIDE_BA_Q01 matches 30 run tellraw @s [{"text":"[Mira]","color":"aqua"},{"text":" : Moins fort. Il nous entendra.","color":"gray"}]
execute if score @s QUEST_SIDE_BA_Q01 matches 101 run tellraw @s [{"text":"[Mira]","color":"aqua"},{"text":" : Hé. Pas trop fort. Les bonnes informations détestent le bruit.","color":"white"}]
execute if score @s QUEST_SIDE_BA_Q01 matches 102 run tellraw @s [{"text":"[Mira]","color":"aqua"},{"text":" : Garde tes distances. C’est plus simple pour tout le monde.","color":"white"}]
