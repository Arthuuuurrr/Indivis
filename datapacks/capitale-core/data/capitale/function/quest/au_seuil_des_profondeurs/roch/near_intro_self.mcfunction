function capitale:player/ensure_runtime_self
scoreboard players set @s QUEST_PROFONDEURS 1
scoreboard players set @s CAP_QUETEACTIVE 5
function capitale:quest/objective/parler_roch_second_ascenseur_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Vous venez du Cœur ? Alors vous arrivez par le chemin que prennent presque tous ceux qui cherchent un toit à prix honnête.","color":"white"}]
scoreboard players set @s CAP_QSEQ 831
scoreboard players set @s CAP_QSEQ_TIMER 20
