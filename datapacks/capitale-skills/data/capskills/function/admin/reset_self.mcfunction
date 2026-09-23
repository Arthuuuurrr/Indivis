# Compat ancienne commande : ne plus faire de reset manuel destructeur.
# Redirection vers le reset Pufferfish afin de rembourser les points quand ils existent côté arbre.
tellraw @s {"text":"[CapSkills 0.8.1] reset_self redirige maintenant vers le respec Pufferfish sûr.","color":"yellow"}
function capskills:admin/pufferfish_reset_skills_self
