# Sentence croisée I — passif : un seul bonus d'exécution par recharge interne.
execute unless entity @s[tag=capskills_0130.double_lame.passive_ready] run return 0
execute if score @s CAPSK_DUAL_PASS_CD matches 1.. run return 0
tag @s add capskills_0130.double_lame.passive_caster
execute as @e[tag=capskills.lame_target,sort=nearest,limit=1] at @s run function capskills_0130:double_lame/passive_check_as_target
tag @s remove capskills_0130.double_lame.passive_caster
