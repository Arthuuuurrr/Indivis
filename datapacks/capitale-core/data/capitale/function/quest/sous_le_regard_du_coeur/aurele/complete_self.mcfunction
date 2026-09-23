
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Vous voilà au seuil des Profondeurs. Descendez si vous cherchez un toit plus accessible ; ceux d’en bas vous guideront mieux que moi à partir d’ici.","color":"white"}]
scoreboard players set @s CAP_QSEQ 721
scoreboard players set @s CAP_QSEQ_TIMER 20
