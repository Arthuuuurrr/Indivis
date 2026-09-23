# LEGACY 0.9.27 : ancienne Égide ciblée désactivée.
# @s = joueur allié éventuellement visé par un ancien chemin ; aucun effet n'est appliqué.
title @s actionbar {"text":"Égide ciblée retirée : l'Égide est désormais une zone.","color":"red"}
execute as @a[tag=capskills.caster,limit=1] run title @s actionbar {"text":"Égide ciblée retirée : utilisez sneak + clic droit pour l'Égide de zone.","color":"red"}
scoreboard players set #hit CAPSK_TMP 1
