# Réparation après dev_unlock_all_self/reset_self des versions 0.7.x/0.8.0.
# Cas visé : tags retirés manuellement mais skills Pufferfish encore considérés comme achetés.
puffish_skills category unlock @s capskills:doctrine
puffish_skills skills reset @s capskills:doctrine
function capskills:admin/_clear_skill_runtime_self
function capskills:integration/sync_self
tellraw @s {"text":"[CapSkills 0.8.1] Réparation effectuée. Si des skills étaient encore enregistrés côté Pufferfish, leurs points ont été remboursés.","color":"gold"}
