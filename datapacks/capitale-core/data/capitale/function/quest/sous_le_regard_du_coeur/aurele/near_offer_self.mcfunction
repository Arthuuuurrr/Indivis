# RC9ak — near distance compatible si CAP_QUETEACTIVE est resté bloqué par une étape précédente.
function capitale:player/ensure_runtime_self
# RC9ag — near distance compatible avec l’état d’attente QUEST_GARDECOEUR=10.
execute if score @s QUEST_GARDEPORT matches 100.. if score @s QUEST_GARDECOEUR matches 0 run function capitale:quest/sous_le_regard_du_coeur/aurele/near_intro_self
execute if score @s QUEST_GARDEPORT matches 100.. if score @s QUEST_GARDECOEUR matches 10 run function capitale:quest/sous_le_regard_du_coeur/aurele/near_intro_self
execute if score @s QUEST_GARDECOEUR matches 25 if score @s CAP_ESCORT_WAIT_CD matches 0 run function capitale:dialogue/sound/parole_guidage_self
execute if score @s QUEST_GARDECOEUR matches 25 if score @s CAP_ESCORT_WAIT_CD matches 0 run tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Je retourne à l’ascenseur. Retrouvez-moi au point de départ quand votre visite sera terminée.","color":"white"}]
execute if score @s QUEST_GARDECOEUR matches 25 if score @s CAP_ESCORT_WAIT_CD matches 0 run scoreboard players set @s CAP_ESCORT_WAIT_CD 100
