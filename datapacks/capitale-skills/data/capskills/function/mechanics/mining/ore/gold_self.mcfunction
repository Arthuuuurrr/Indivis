execute store result score #roll CAPSK_TMP run random value 1..100
execute if entity @s[tag=capskills.mineur.bonus_ores.r2] if score #roll CAPSK_TMP matches ..15 run function capskills:mechanics/mining/proc/gold_self
execute unless entity @s[tag=capskills.mineur.bonus_ores.r2] if entity @s[tag=capskills.mineur.bonus_ores.r1] if score #roll CAPSK_TMP matches ..8 run function capskills:mechanics/mining/proc/gold_self
