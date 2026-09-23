# Compatibilité historique : clôture narrative de la quête de spawn sans rang ni réputation.
# RC9l lazy init QUEST : valeurs créées uniquement avant lecture.
scoreboard players add @s QUEST_SPAWN 0
execute if score @s QUEST_SPAWN matches 40..99 run function capitale:quest/spawn_dirigeable_couronne/geraud_quai_complete_self
execute unless score @s QUEST_SPAWN matches 40..99 run tellraw @s {"text":"La quête de spawn ne peut être clôturée qu’après l’arrivée à quai.","color":"gray"}
