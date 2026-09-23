function capitale:player/ensure_runtime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"──────── Contrat profils / sauvegarde ────────","color":"gold","bold":true}
tellraw @s {"text":"Le mod doit sauvegarder les objectifs par conventions de nommage, pas par liste Java figée.","color":"gray"}
tellraw @s {"text":"Sources persistantes recommandées : CAP_, REP_, QUEST_, ACCESS_, GUILD_, JOB_, FACTION_, CAPSK_ ; avec exclusions runtime listées dans data/capitale/profile_persistence/policy.json.","color":"yellow"}
tellraw @s {"text":"Après restauration d'un slot, appeler capitale:profiles/after_load_self pour reconstruire rangs de dialogue, criminalité, justice, accès, tags, cooldowns et préfixe.","color":"aqua"}
tellraw @s [{"text":"[","color":"dark_gray"},{"text":"Hooks mod","color":"green","click_event":{"action":"run_command","command":"/function capitale:profiles/mod_hooks_self"}},{"text":"]","color":"dark_gray"}]
