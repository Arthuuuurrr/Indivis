function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Lanternes du Globe]","color":"yellow"},{"text":" : La lumière se remet en phase.","color":"white"},{"score":{"name":"@s","objective":"CAP_GLOBE_DONE"},"color":"gold"},{"text":"/8 lanternes recalibrées.","color":"white"}]
