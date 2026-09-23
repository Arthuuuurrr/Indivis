function capitale:player/ensure_runtime_self
scoreboard players set @s QUEST_GARDEPORT 1
scoreboard players set @s CAP_QUETEACTIVE 4
function capitale:quest/objective/parler_leovic_quais_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Vous descendez du dirigeable de la Couronne ? Alors vous êtes nouveau au Port. Si personne ne vous a encore inscrit, je peux vous mener au magistrat.","color":"white"}]
scoreboard players set @s CAP_QSEQ 502
scoreboard players set @s CAP_QSEQ_TIMER 20
