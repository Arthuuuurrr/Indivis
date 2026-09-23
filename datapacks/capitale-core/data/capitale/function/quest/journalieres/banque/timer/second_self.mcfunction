scoreboard players remove @s CAP_BANQUE_TIMER 1
execute if score @s CAP_BANQUE_TIMER matches 45 run title @s actionbar {"text":"La Banque — 45 secondes restantes.","color":"gold"}
execute if score @s CAP_BANQUE_TIMER matches 30 run title @s actionbar {"text":"La Banque — 30 secondes restantes.","color":"gold"}
execute if score @s CAP_BANQUE_TIMER matches 15 run title @s actionbar {"text":"La Banque — 15 secondes restantes.","color":"yellow"}
execute if score @s CAP_BANQUE_TIMER matches 10 run title @s actionbar {"text":"Le registre va bientôt être clos.","color":"red"}
execute if score @s CAP_BANQUE_TIMER matches 5 run title @s actionbar {"text":"5 secondes avant clôture.","color":"red"}
execute if score @s CAP_BANQUE_TIMER matches 0 run function capitale:quest/journalieres/banque/timer/expired_self
