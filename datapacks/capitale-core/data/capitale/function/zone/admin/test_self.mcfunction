function capitale:dialogue/sound/parole_simple_self
tellraw @s [{"text":"[Zones protégées]","color":"gold","bold":true},{"text":" État joueur : ","color":"white"},{"text":"inside=","color":"gray"},{"selector":"@s[tag=cap_zone_inside_any]","color":"green"},{"text":" forced=","color":"gray"},{"selector":"@s[tag=cap_zone_forced_adventure]","color":"aqua"},{"text":" bypass=","color":"gray"},{"selector":"@s[tag=cap_zone_bypass]","color":"light_purple"}]
tellraw @s [{"text":"Distance² dernière zone testée : ","color":"gray"},{"score":{"name":"@s","objective":"CAP_ZONE_DIST2"},"color":"white"}]
