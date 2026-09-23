# CapSkills 0.9.4 — Sentence d’exécution. @s = attaquant, cible = capskills.lame_target.
scoreboard players set #exec_hp10 CAPSK_TMP 9999
execute store result score #exec_hp10 CAPSK_TMP run data get entity @e[tag=capskills.lame_target,limit=1,sort=nearest] Health 10
execute if score #exec_hp10 CAPSK_TMP matches ..80 run function capskills:skill/lame/execution_apply_self
execute if score #exec_hp10 CAPSK_TMP matches 81.. run title @s actionbar {"text":"Sentence retenue : cible trop robuste.","color":"gray"}
