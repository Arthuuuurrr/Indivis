function capitale:player/ensure_runtime_self
tellraw @s [{"text":"[Debug Aurèle Near] ","color":"gold"},{"text":"QUEST_GARDEPORT=","color":"gray"},{"score":{"name":"@s","objective":"QUEST_GARDEPORT"},"color":"white"},{"text":" QUEST_GARDECOEUR=","color":"gray"},{"score":{"name":"@s","objective":"QUEST_GARDECOEUR"},"color":"white"},{"text":" QUEST_PROLOGUE=","color":"gray"},{"score":{"name":"@s","objective":"QUEST_PROLOGUE"},"color":"white"},{"text":" CAP_QUETEACTIVE=","color":"gray"},{"score":{"name":"@s","objective":"CAP_QUETEACTIVE"},"color":"white"}]
function capitale:npc/aurele/near_self
