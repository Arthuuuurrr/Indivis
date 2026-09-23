function capitale:player/ensure_runtime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"──────── Registre des personnages ────────","color":"gold","bold":true}
tellraw @s {"text":"— Principe","color":"yellow"}
tellraw @s {"text":"Ce menu prépare le futur système de profils/personnages. Pour l’instant, il ne change pas encore la playerdata.","color":"gray"}
tellraw @s {"text":"Objectif final : conserver plusieurs personnages sur un même compte, avec classe, progression RP, CapSkills, inventaire et position séparés via mod serveur.","color":"gray"}
tellraw @s {"text":"— Statut actuel","color":"yellow"}
tellraw @s [{"text":"Profil actif déclaré : ","color":"gray"},{"score":{"name":"@s","objective":"CAP_PROFILE_ACTIVE"},"color":"aqua"}]
tellraw @s [{"text":"Verrou de changement : ","color":"gray"},{"score":{"name":"@s","objective":"CAP_PROFILE_LOCK"},"color":"red"}]
tellraw @s {"text":"— Actions disponibles","color":"yellow"}
tellraw @s [{"text":"[","color":"dark_gray"},{"text":"Règles de changement","color":"green","click_event":{"action":"run_command","command":"/function capitale:profiles/rules_self"},"hover_event":{"action":"show_text","value":"Voir les règles RP et anti-duplication prévues."}},{"text":"] ","color":"dark_gray"},{"text":"[","color":"dark_gray"},{"text":"Hooks mod","color":"aqua","click_event":{"action":"run_command","command":"/function capitale:profiles/mod_hooks_self"},"hover_event":{"action":"show_text","value":"Voir les fonctions que le mod doit appeler."}},{"text":"] ","color":"dark_gray"},{"text":"[","color":"dark_gray"},{"text":"Contrat persistance","color":"yellow","click_event":{"action":"run_command","command":"/function capitale:profiles/persistence_contract_self"},"hover_event":{"action":"show_text","value":"Voir la politique de sauvegarde par préfixes."}},{"text":"] ","color":"dark_gray"},{"text":"[","color":"dark_gray"},{"text":"Retour InfoRP","color":"gray","click_event":{"action":"run_command","command":"/trigger InfoRP set 1"},"hover_event":{"action":"show_text","value":"Retour au dossier RP."}},{"text":"]","color":"dark_gray"}]
tellraw @s {"text":"— Note","color":"yellow"}
tellraw @s {"text":"Le changement réel de personnage devra être exécuté par un mod, pas par ce menu seul, afin d’éviter duplication ou corruption d’inventaire.","color":"dark_gray"}
