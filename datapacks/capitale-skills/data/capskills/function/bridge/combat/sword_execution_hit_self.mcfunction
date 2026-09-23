# Sentence croisée I — amélioration passive des attaques normales à deux armes.
execute if entity @s[tag=capskills.lame.execution.r1,tag=capskills_0130.double_lame.passive_ready] unless score @s CAPSK_DUAL_PASS_CD matches 1.. run function capskills_0130:double_lame/passive_hit_self
