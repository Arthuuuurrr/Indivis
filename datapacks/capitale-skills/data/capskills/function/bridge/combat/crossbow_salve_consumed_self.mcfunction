# CapSkills 0.9.15 — consommation unique de Salve préparée.
# @s = arbalétrier. Appelé par le jar dès que le prochain carreau reçoit son boost.
scoreboard players set @s CAPSK_CROSS_READY 0
tag @s remove capskills.arbalete.salve.ready
tag @s remove capskills.arbalete.salve.channeling
tag @s remove capskills.arbalete.salve.forced_channel
execute if entity @s[tag=capskills.arbalete.salve.fired] run title @s actionbar {"text":"Salve libérée — carreau accéléré","color":"yellow"}

scoreboard players set @s CAPSK_CROSS_SHOT_T 0
scoreboard players set @s CAPSK_CROSS_USED 0
tag @s remove capskills.arbalete.salve.pending_clear
tag @s remove capskills.arbalete.salve.just_shot
tag @s remove capskills.arbalete.salve.fired
