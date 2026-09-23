# Fallback — Marchand d'artefacts.
# Si cette fonction apparaît en jeu, le bouton Open Trading Menu n’est pas encore configuré dans EasyNPC.
function capitale:shop/easynpc_open_trading_hint_self
tellraw @s [{"text": "[Marchand d'artefacts]", "color": "dark_purple"}, {"text": " : Pour les ventes réelles, utilise les items CapSkills donnés par les fonctions capskills:give/mod/*, puis place-les dans les trades EasyNPC.", "color": "gray"}]
