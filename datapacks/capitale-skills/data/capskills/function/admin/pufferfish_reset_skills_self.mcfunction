# Admin/operator only. Reset propre : verrouille les skills Pufferfish et rembourse les points dépensés.
# Le nettoyage local retire aussi les anciens tags ajoutés manuellement par les versions 0.7.x/0.8.0.
puffish_skills category unlock @s capskills:doctrine
puffish_skills skills reset @s capskills:doctrine
function capskills:admin/_clear_skill_runtime_self
function capskills:integration/sync_self
tellraw @s {"text":"[CapSkills 0.8.1] Respec Pufferfish effectué : skills verrouillés, points remboursés par le mod, tags runtime nettoyés.","color":"red"}
