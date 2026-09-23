function capitale:player/ensure_runtime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"──────── Hooks mod profils ────────","color":"gold","bold":true}
tellraw @s {"text":"— À appeler avant sauvegarde du profil actif","color":"yellow"}
tellraw @s [{"text":"/function capitale:profiles/before_save_self","color":"aqua","click_event":{"action":"suggest_command","command":"/function capitale:profiles/before_save_self"}}]
tellraw @s {"text":"— À appeler après chargement du profil choisi","color":"yellow"}
tellraw @s [{"text":"/function capitale:profiles/after_load_self","color":"aqua","click_event":{"action":"suggest_command","command":"/function capitale:profiles/after_load_self"}}]
tellraw @s {"text":"— Contrat de persistance","color":"yellow"}
tellraw @s [{"text":"/function capitale:profiles/persistence_contract_self","color":"green","click_event":{"action":"run_command","command":"/function capitale:profiles/persistence_contract_self"}}]
tellraw @s {"text":"Le mod doit sauvegarder les scores par préfixes et laisser le datapack recalculer les valeurs dérivées après chargement.","color":"gray"}
tellraw @s [{"text":"[","color":"dark_gray"},{"text":"Retour profils","color":"gray","click_event":{"action":"run_command","command":"/trigger InfoRP set 20"}},{"text":"]","color":"dark_gray"}]
