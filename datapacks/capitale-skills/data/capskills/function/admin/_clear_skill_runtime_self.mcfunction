# CapSkills 0.8.1 — nettoyage local après reset Pufferfish ou ancienne commande admin.
function capskills:admin/_clear_skill_tags_self
tag @s remove capskills.caster
tag @e[tag=capskills.magic_target] remove capskills.magic_target
tag @e[tag=capskills.magic_probe] remove capskills.magic_probe
function capskills:admin/_clear_runtime_scores_self
