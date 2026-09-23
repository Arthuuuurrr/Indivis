# Sentence croisée II — ruée fluide vers la cible.
# À chaque impact, le joueur est réorienté. La ruée n'est lancée qu'une fois par sort.
# Elle est découpée en cinq petites impulsions horizontales décroissantes afin d'éviter
# l'effet de téléportation instantanée tout en restant compatible avec le datapack.

tp @s ~ ~ ~ facing entity @e[tag=capskills_0119.lance_longue.native_target,sort=nearest,limit=1] eyes
execute unless entity @s[tag=capskills_0130.double_lame.dash_done] if entity @e[tag=capskills_0119.lance_longue.native_target,sort=nearest,limit=1,distance=1.45..5.75] run function capskills_0130:double_lame/dash_start_self
