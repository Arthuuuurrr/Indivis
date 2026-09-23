# Le verrouillage de cible est conservé dans l'horloge Java, pas dans des tags globaux.
scoreboard players add @s CAPSK_LANCE_CD 0
execute if entity @s[tag=capskills_0119.lance_longue.channeling] run title @s actionbar {"text":"Lance longue : préparation déjà en cours.","color":"red"}
execute if entity @s[tag=capskills_0119.lance_longue.active] run title @s actionbar {"text":"Lance longue déjà active.","color":"red"}
execute unless entity @s[tag=capskills_0119.lance_longue.channeling] unless entity @s[tag=capskills_0119.lance_longue.active] if score @s CAPSK_LANCE_CD matches 1.. run title @s actionbar [{"text":"Lance longue en recharge : ","color":"red"},{"score":{"name":"@s","objective":"CAPSK_LANCE_CD"},"color":"red"},{"text":" s","color":"red"}]
execute unless entity @s[tag=capskills_0119.lance_longue.channeling] unless entity @s[tag=capskills_0119.lance_longue.active] if score @s CAPSK_LANCE_CD matches 0 run function capskills_0119:lance_longue/begin_self
