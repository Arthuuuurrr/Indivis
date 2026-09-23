# Hook mod profils — appelé AVANT sauvegarde du profil actif.
# À garder silencieux autant que possible pour éviter le spam en cas d’appel automatique.
function capitale:profiles/pre_save_runtime_flush_self
scoreboard players set @s CAP_PROFILE_LOCK 1
# Le mod doit ensuite sauvegarder l'état du slot selon la politique par préfixes : voir data/capitale/profile_persistence/policy.json.
