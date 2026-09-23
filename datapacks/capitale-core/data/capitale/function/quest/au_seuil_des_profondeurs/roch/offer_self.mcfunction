scoreboard players set @s QUEST_PROFONDEURS 1
function capitale:quest/set_active/profondeurs_self
function capitale:quest/objective/parler_roch_second_ascenseur_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Vous êtes bien arrivé dans les Profondeurs. Ici, les loyers descendent souvent avant la lumière.","color":"white"}]
scoreboard players set @s CAP_QSEQ 833
scoreboard players set @s CAP_QSEQ_TIMER 20
