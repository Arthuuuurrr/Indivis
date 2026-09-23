# Admin/operator only. Reset total : efface catégorie, points, XP Pufferfish et skills.
# À utiliser pour repartir de zéro, pas pour récupérer des points.
puffish_skills category erase @s capskills:doctrine
function capskills:admin/_clear_skill_runtime_self
scoreboard players set @s CAPSK_XP 0
scoreboard players set @s CAPSK_LEVEL 0
tellraw @s {"text":"[CapSkills 0.8.1] Catégorie doctrine effacée : points/skills/XP Pufferfish et scores CapSkills remis à zéro.","color":"dark_red"}
