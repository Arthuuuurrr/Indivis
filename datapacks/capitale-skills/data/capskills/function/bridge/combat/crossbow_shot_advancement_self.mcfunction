# CapSkills 0.9.20 — déclencheur réel de tir d'arbalète.
# @s = joueur qui vient de tirer avec une arbalète, via advancement minecraft:shot_crossbow.
# Ce chemin ne dépend plus de minecraft.used:minecraft.crossbow, qui peut se déclencher au chargement plutôt qu'au tir utile.
advancement revoke @s only capskills:internal/crossbow_shot
function capskills:integration/ensure_current_self
scoreboard players set @s CAPSK_CROSS_ADV_T 10
scoreboard players set @s CAPSK_CROSS_DIRECT_HIT 0
tag @s add capskills.crossbow.adv_shot_window
# Si la Salve était armée au moment du tir, on garde un marqueur temporaire avant consommation.
execute if entity @s[tag=capskills.arbalete.salve.ready] run tag @s add capskills.arbalete.salve.just_shot
execute if entity @s[tag=capskills.arbalete.salve.ready] run tag @s add capskills.arbalete.salve.pending_clear
execute if entity @s[tag=capskills.arbalete.salve.ready] run scoreboard players set @s CAPSK_CROSS_SHOT_T 8
# 0.9.23 : tag des projectiles proches uniquement.
# Le fallback direct/raycast immédiat est désactivé : il pouvait marquer un mob voisin au lieu de la cible réellement touchée.
function capskills:bridge/combat/crossbow_tag_near_projectiles_self
# Consommation stricte après avoir copié l'état Salve sur just_shot/pending_clear.
execute if entity @s[tag=capskills.arbalete.salve.just_shot] run function capskills:bridge/combat/crossbow_salve_consume_keep_window_self
# 0.9.22 debug désactivé : tellraw @s {"text":"[CapSkills DBG] shot_crossbow déclenché : fallback advancement actif.","color":"dark_aqua"}
