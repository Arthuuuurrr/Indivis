# Entrée déjà filtrée par le jar 1.6.5 selon l'identifiant réel de l'arme tenue.
execute if entity @s[tag=capskills.lame.lance_longue.r1] run function capskills_0119:lance_longue/attempt_self
execute unless entity @s[tag=capskills.lame.lance_longue.r1] run title @s actionbar {"text":"Lance longue : perk non débloqué.","color":"red"}
