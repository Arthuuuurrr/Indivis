# 0.9.35 — donne les catalyseurs et équipements d’entraînement utiles aux tests.
function capskills:give/mod/all_items_self
function capskills:give/starter/training_sword_self
function capskills:give/starter/training_shield_self
function capskills:give/starter/training_bow_self
function capskills:give/starter/training_crossbow_self
tellraw @s {"text":"[CapSkills] Catalyseurs et équipement d’entraînement donnés.","color":"gold"}
