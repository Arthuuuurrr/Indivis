function capitale:player/ensure_runtime_self
# RC9af — near distance compatible avec l’état d’attente QUEST_GARDEPORT=10.
execute if score @s QUEST_SPAWN matches 100 if score @s QUEST_GARDEPORT matches 0 run function capitale:quest/le_registre_du_port/leovic/offer_self
execute if score @s QUEST_SPAWN matches 100 if score @s QUEST_GARDEPORT matches 10 run function capitale:quest/le_registre_du_port/leovic/offer_self
