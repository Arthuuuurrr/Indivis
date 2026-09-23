# Fallback datapack : le menu de commerce réel est une action EasyNPC, pas une commande vanilla.
# Configuration recommandée dans EasyNPC : bouton "Ouvrir la boutique" -> action Open Trading Menu.
function capitale:dialogue/sound/parole_simple_self
tellraw @s [{"text":"[Commerce]","color":"gold"},{"text":" : Le menu d’achat doit être ouvert par l’action EasyNPC ","color":"gray"},{"text":"Open Trading Menu","color":"yellow"},{"text":". Si vous voyez ce message en jeu, le bouton EasyNPC n’est pas encore configuré sur ce PNJ.","color":"gray"}]
