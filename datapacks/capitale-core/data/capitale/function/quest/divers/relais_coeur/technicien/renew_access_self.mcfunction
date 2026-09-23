
scoreboard players set @s CAP_PASS_ZONE 50
scoreboard players set @s CAP_PASS_TIMER 12000
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Encore ? Les relais ne se déplacent pas, pourtant. Reprenez ce laissez-passer — dix minutes de plus, et soyez plus prompt cette fois.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès temporaire] Cœur — passage renouvelé pour dix minutes.","color":"green"}
execute at @s run playsound minecraft:block.beacon.activate master @s ~ ~ ~ 0.55 0.95
