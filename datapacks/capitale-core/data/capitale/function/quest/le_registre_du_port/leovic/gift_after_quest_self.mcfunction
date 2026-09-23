function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_LEOVIC_GIFT 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Vous revoilà. Tenez :","color":"white"},{"text":"50 Martins d’Or","color":"yellow"},{"text":". Ce n’est pas une fortune, mais les premiers jours dans une ville pareille coûtent toujours plus qu’on ne l’imagine. Gardez-les.","color":"white"}]
give @s capitale_currency:martin_dor 50
function capitale:rewards/daily_bonus/roll/port_self
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.15
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Et si vous cherchez un travail honnête, repassez parfois. Les quais manquent rarement de bras, d’yeux attentifs… ou de gens capables de garder leur sang-froid.","color":"white"}]
