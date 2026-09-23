# CapSkills 0.9.152 — synchronisation complète, appelée seulement sur changement ou audit de sécurité.
function capskills:integration/sync_skill_markers_self
function capskills:integration/sync_self
function capskills:integration/grant_missing_items_self
function capskills:compat/combat_roll/sync_self
scoreboard players set @s CAPSK_SYNC_DIRTY 0
