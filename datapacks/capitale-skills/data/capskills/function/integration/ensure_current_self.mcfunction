# CapSkills 0.9.152 — chemin léger pour les sorts : deux tests, synchronisation seulement si nécessaire.
execute unless score @s CAPSK_RUNTIME_VER matches 152 run function capskills:integration/sync_all_self
execute if score @s CAPSK_SYNC_DIRTY matches 1.. run function capskills:integration/sync_all_self
