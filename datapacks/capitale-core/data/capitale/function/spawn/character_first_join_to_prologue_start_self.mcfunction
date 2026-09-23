# Entrée dédiée au système multi-personnages du mod capitale_rp_hud.
# Ne remplace pas le prologue classique : elle ajoute seulement le marqueur
# qui force le menu d'identité au début du prologue d'un nouveau slot.
tag @s add capitale_identity_mandatory
function capitale:spawn/first_join_to_prologue_start_self
