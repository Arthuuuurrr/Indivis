function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : Les bordereaux ne se sont pas encore assez emmêlés pour justifier de vous rappeler. Revenez après la prochaine rotation des quais.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Commis du Port]","color":"yellow"},{"text":" : Les papiers ont eu leur part d’ordre pour aujourd’hui. Repassez à la prochaine rotation des quais.","color":"white"}]
