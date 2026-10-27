# PQ02-R1 — Don personnel unique, ancien bonus de port conservé, aucun effet de réputation
execute unless score @s CAP_QDIALOG_OWNER matches 97 run return 0
execute unless score @s hcq_pq02 matches 90..100 run return 0
execute unless score @s CAP_PQ2_OFFER matches 1 run return 0
execute if score @s CAP_LEOVIC_GIFT matches 1.. run return 0
scoreboard players set @s CAP_LEOVIC_GIFT 1
give @s capitale_currency:martin_dor 50
function capitale:rewards/daily_bonus/roll/port_self
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.15
return 1
