# CapSkills 0.9.56 — placement sûr de la cible de Poigne.
# @s = tank ; cible taggée capskills.mod_pull_target.
# On ignore le pitch du joueur pour éviter de projeter la cible dans le sol/plafond.
# Ordre : devant le tank, légèrement plus près, latéral droit/gauche, puis position du tank si sûre.
scoreboard players set #placed CAPSK_TMP 0

# 2.6 blocs devant, yaw seulement.
execute if score #placed CAPSK_TMP matches 0 rotated as @s rotated ~ 0 positioned ^ ^ ^2.6 if block ~ ~ ~ #capskills:grab_safe_space if block ~ ~1 ~ #capskills:grab_safe_space run function capskills:skill/rempart/grab_tp_here_self

# 2.2 blocs devant.
execute if score #placed CAPSK_TMP matches 0 rotated as @s rotated ~ 0 positioned ^ ^ ^2.2 if block ~ ~ ~ #capskills:grab_safe_space if block ~ ~1 ~ #capskills:grab_safe_space run function capskills:skill/rempart/grab_tp_here_self

# 1.6 bloc devant, utile en couloir.
execute if score #placed CAPSK_TMP matches 0 rotated as @s rotated ~ 0 positioned ^ ^ ^1.6 if block ~ ~ ~ #capskills:grab_safe_space if block ~ ~1 ~ #capskills:grab_safe_space run function capskills:skill/rempart/grab_tp_here_self

# Décalage latéral droit.
execute if score #placed CAPSK_TMP matches 0 rotated as @s rotated ~ 0 positioned ^0.8 ^ ^2.0 if block ~ ~ ~ #capskills:grab_safe_space if block ~ ~1 ~ #capskills:grab_safe_space run function capskills:skill/rempart/grab_tp_here_self

# Décalage latéral gauche.
execute if score #placed CAPSK_TMP matches 0 rotated as @s rotated ~ 0 positioned ^-0.8 ^ ^2.0 if block ~ ~ ~ #capskills:grab_safe_space if block ~ ~1 ~ #capskills:grab_safe_space run function capskills:skill/rempart/grab_tp_here_self

# Légèrement au-dessus du sol devant le tank, pour éviter un demi-bloc ou une bordure basse.
execute if score #placed CAPSK_TMP matches 0 rotated as @s rotated ~ 0 positioned ^ ^0.35 ^2.0 if block ~ ~ ~ #capskills:grab_safe_space if block ~ ~1 ~ #capskills:grab_safe_space run function capskills:skill/rempart/grab_tp_here_self

# Dernier recours : sur la position du tank uniquement si l'espace est sûr.
execute if score #placed CAPSK_TMP matches 0 at @s if block ~ ~ ~ #capskills:grab_safe_space if block ~ ~1 ~ #capskills:grab_safe_space run function capskills:skill/rempart/grab_tp_here_self
