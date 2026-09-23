# Préparation courte de Sentence croisée. Le jar a déjà validé la paire et verrouillé la cible.
tag @s add capskills_0128.double_lame.channeling
tag @s remove capskills_0128.double_lame.active
execute at @s run particle minecraft:crit ~ ~1.0 ~ 0.35 0.25 0.35 0.02 12 force @a[distance=..20]
execute at @s run playsound minecraft:item.armor.equip_chain player @a[distance=..20] ~ ~ ~ 0.55 1.65 0
effect give @s minecraft:slowness 1 0 true
execute if entity @s[tag=capskills_0128.double_lame.synergy_mixed] run title @s actionbar {"text":"Sentence croisée : paire mixte — exécution sous 25 %.","color":"red"}
execute if entity @s[tag=capskills_0128.double_lame.synergy_matched] run title @s actionbar {"text":"Sentence croisée : paire de finesse assortie — exécution sous 30 %.","color":"red"}
execute if entity @s[tag=capskills_0128.double_lame.synergy_dagger_finesse] run title @s actionbar {"text":"Sentence croisée : dague/rapière/griffe + autre finesse — exécution sous 35 %.","color":"dark_red"}
execute if entity @s[tag=capskills_0128.double_lame.synergy_double_dagger] run title @s actionbar {"text":"Sentence croisée : paire dague/rapière/griffe — finisher maximal, exécution sous 40 %.","color":"dark_red"}
