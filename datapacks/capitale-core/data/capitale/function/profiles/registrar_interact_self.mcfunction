# Conversation RP basique pour un PNJ de registre des personnages.
# À appeler depuis EasyNPC avec executeAsUser=true.
function capitale:player/ensure_runtime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"──────── Greffe des personnages ────────","color":"gold","bold":true}
tellraw @s [{"text":"[Greffier du registre]","color":"#FF8C00"},{"text":" : Le registre des arrivées conserve les identités, les affectations et les changements de rôle. Pour l’instant, je peux seulement vous présenter le principe du futur système de profils.","color":"white"}]
tellraw @s {"text":"— Options","color":"yellow"}
tellraw @s [{"text":"[","color":"dark_gray"},{"text":"Consulter le registre","color":"green","click_event":{"action":"run_command","command":"/trigger InfoRP set 20"},"hover_event":{"action":"show_text","value":"Ouvrir le menu des profils/personnages."}},{"text":"] ","color":"dark_gray"},{"text":"[","color":"dark_gray"},{"text":"Règles de changement","color":"aqua","click_event":{"action":"run_command","command":"/function capitale:profiles/rules_self"},"hover_event":{"action":"show_text","value":"Voir les règles prévues contre les abus et la duplication."}},{"text":"] ","color":"dark_gray"},{"text":"[","color":"dark_gray"},{"text":"Partir","color":"gray","click_event":{"action":"run_command","command":"/function capitale:profiles/registrar_leave_self"},"hover_event":{"action":"show_text","value":"Fermer la conversation."}},{"text":"]","color":"dark_gray"}]
