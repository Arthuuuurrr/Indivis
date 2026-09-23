# Flush central avant sauvegarde de profil.
# Objectif : normaliser les valeurs sources persistantes sans écrire de données temporaires.
# Le mod doit appeler capitale:profiles/before_save_self avant de capturer le snapshot du slot.
function capitale:player/ensure_runtime_self
function capitale:bounds/rangsocial_self
function capitale:bounds/crime_self
function capitale:bounds/reputation_all_self
function capitale:logic/classify/base_self
function capitale:access/recalculate_by_rank_silent_self
function capitale:access/sync_tags_self
function capitale:quest/journalieres/cooldown/sync_self
function capitale:display/prefix/sync_self
