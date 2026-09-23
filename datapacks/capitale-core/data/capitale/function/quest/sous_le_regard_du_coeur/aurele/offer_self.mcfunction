scoreboard players set @s QUEST_GARDECOEUR 1
function capitale:quest/set_active/gardecoeur_self
function capitale:quest/objective/parler_aurele_sortie_ascenseur_coeur_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Halte. L’ascenseur du Port vous dépose au milieu du Cœur. Ici, les passages servent à maintenir l’ordre des portails, non à laisser errer les nouveaux venus.","color":"white"}]
scoreboard players set @s CAP_QSEQ 701
scoreboard players set @s CAP_QSEQ_TIMER 20
