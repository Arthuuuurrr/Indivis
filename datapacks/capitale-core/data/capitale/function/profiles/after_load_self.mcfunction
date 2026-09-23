# Hook futur mod capitale_profiles — appelé APRÈS chargement du profil choisi.
function capitale:profiles/resync_after_profile_load_self
tellraw @s {"text":"Profil chargé. Données RP resynchronisées.","color":"green"}
