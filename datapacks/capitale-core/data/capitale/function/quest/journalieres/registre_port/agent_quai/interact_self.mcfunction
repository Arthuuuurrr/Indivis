function capitale:player/ensure_runtime_self
execute if score @s QUEST_DAILY_REGISTRE_PORT matches 1 run function capitale:quest/journalieres/registre_port/agent_quai/confirm_self
execute if score @s QUEST_DAILY_REGISTRE_PORT matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_DAILY_REGISTRE_PORT matches 2 run tellraw @s [{"text":"[Agent de quai]","color":"yellow"},{"text":" : Je vous ai déjà donné ma confirmation. Le Commis du Port attend votre retour.","color":"white"}]
execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 1..2 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DAILY_REGISTRE_PORT matches 1..2 run tellraw @s [{"text":"[Agent de quai]","color":"yellow"},{"text":" : Si cela concerne une cargaison, voyez le comptoir. Si cela concerne un registre, voyez d’abord qui vous l’a confié.","color":"white"}]
