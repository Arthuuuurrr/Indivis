# CapSkills 0.9.64 EXP — attribution Combat Roll.
# Nécessite le mod combat_roll chargé. Si le mod est absent, ces commandes peuvent afficher des erreurs d’attribut inconnu.
# Base mod : distance 3.0, recharge 20.0, count 1. Les valeurs restent faibles pour ne pas écraser Rempart/Ombre.
scoreboard players set @s CAPSK_CR_DIST 300
scoreboard players set @s CAPSK_CR_RECH 20
scoreboard players set @s CAPSK_CR_COUNT 1
attribute @s combat_roll:distance base set 3.0
attribute @s combat_roll:recharge base set 20.0
attribute @s combat_roll:count base set 1

# Bonus existants : les branches déjà mobiles deviennent utiles sans imposer un nouveau skill.
execute if entity @s[tag=capskills.skill.trait_harceleur_1] run attribute @s combat_roll:recharge base set 21.0
execute if entity @s[tag=capskills.skill.trait_harceleur_1] run scoreboard players set @s CAPSK_CR_RECH 21
execute if entity @s[tag=capskills.skill.trait_harceleur_2] run attribute @s combat_roll:distance base set 3.15
execute if entity @s[tag=capskills.skill.trait_harceleur_2] run attribute @s combat_roll:recharge base set 22.0
execute if entity @s[tag=capskills.skill.trait_harceleur_2] run scoreboard players set @s CAPSK_CR_DIST 315
execute if entity @s[tag=capskills.skill.trait_harceleur_2] run scoreboard players set @s CAPSK_CR_RECH 22
execute if entity @s[tag=capskills.skill.ombre_eclaireur_2] run attribute @s combat_roll:distance base set 3.20
execute if entity @s[tag=capskills.skill.ombre_eclaireur_2] run scoreboard players set @s CAPSK_CR_DIST 320

# Nouveaux skills 0.9.64 EXP.
execute if entity @s[tag=capskills.combat_roll.lame.r1] run attribute @s combat_roll:distance base set 3.20
execute if entity @s[tag=capskills.combat_roll.lame.r1] run attribute @s combat_roll:recharge base set 21.5
execute if entity @s[tag=capskills.combat_roll.lame.r1] run scoreboard players set @s CAPSK_CR_DIST 320
execute if entity @s[tag=capskills.combat_roll.lame.r1] run scoreboard players set @s CAPSK_CR_RECH 21
execute if entity @s[tag=capskills.combat_roll.trait.r1] run attribute @s combat_roll:distance base set 3.25
execute if entity @s[tag=capskills.combat_roll.trait.r1] run attribute @s combat_roll:recharge base set 22.0
execute if entity @s[tag=capskills.combat_roll.trait.r1] run scoreboard players set @s CAPSK_CR_DIST 325
execute if entity @s[tag=capskills.combat_roll.trait.r1] run scoreboard players set @s CAPSK_CR_RECH 22
execute if entity @s[tag=capskills.combat_roll.ombre.r1] run attribute @s combat_roll:distance base set 3.40
execute if entity @s[tag=capskills.combat_roll.ombre.r1] run attribute @s combat_roll:recharge base set 22.5
execute if entity @s[tag=capskills.combat_roll.ombre.r1] run scoreboard players set @s CAPSK_CR_DIST 340
execute if entity @s[tag=capskills.combat_roll.ombre.r1] run scoreboard players set @s CAPSK_CR_RECH 22

# Cap de charge supplémentaire : réservé Ombre avancée pour éviter une mobilité générale trop forte.
execute if entity @s[tag=capskills.combat_roll.ombre.r1,tag=capskills.skill.ombre_dissimulation_2] run attribute @s combat_roll:count base set 2
execute if entity @s[tag=capskills.combat_roll.ombre.r1,tag=capskills.skill.ombre_dissimulation_2] run scoreboard players set @s CAPSK_CR_COUNT 2
