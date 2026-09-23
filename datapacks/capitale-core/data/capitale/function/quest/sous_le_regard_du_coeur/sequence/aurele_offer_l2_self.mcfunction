function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Les Cercles commerciaux sont devant nous. La Couronne garde le reste fermé, par ordre de l’Empereur. Si vous venez d’arriver, je peux vous conduire.","color":"white"}]
function capitale:quest/sous_le_regard_du_coeur/aurele/open_choices_self
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
