# RC9ah — appelé lorsque le guide/Aurèle ont terminé le retour progressif au point A.
scoreboard players set @s QUEST_GARDECOEUR 30
scoreboard players set @s CAP_QUETEACTIVE 3
scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Je suis de retour à l’ascenseur. Lorsque vous serez prêt à descendre vers les Profondeurs, venez me parler.","color":"white"}]
function capitale:quest/objective/parler_aurele_sortie_ascenseur_coeur_self
