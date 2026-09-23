# RC9ak — near distance compatible si CAP_QUETEACTIVE est resté bloqué par une étape précédente.
function capitale:player/ensure_runtime_self
# RC9ag — near distance compatible avec l’état d’attente QUEST_PROFONDEURS=10.
execute if score @s QUEST_GARDECOEUR matches 100.. if score @s QUEST_PROFONDEURS matches 0 run function capitale:quest/au_seuil_des_profondeurs/roch/near_intro_self
execute if score @s QUEST_GARDECOEUR matches 100.. if score @s QUEST_PROFONDEURS matches 10 run function capitale:quest/au_seuil_des_profondeurs/roch/near_intro_self
