function capitale:player/ensure_runtime_self
scoreboard players set @s QUEST_GARDECOEUR 1
scoreboard players set @s CAP_QUETEACTIVE 3
function capitale:quest/objective/parler_aurele_sortie_ascenseur_coeur_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Halte. L’ascenseur du Port vous dépose au milieu du Cœur. Ici, la cité laisse passer ; elle ne laisse pas errer.","color":"white"}]
scoreboard players set @s CAP_QSEQ 731
scoreboard players set @s CAP_QSEQ_TIMER 20
