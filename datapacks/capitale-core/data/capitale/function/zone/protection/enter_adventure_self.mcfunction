tag @s add cap_zone_forced_adventure
gamemode adventure @s
execute if entity @s[tag=cap_zone_inside_couronne] run title @s actionbar {"text":"Zone protégée de la Couronne — mode aventure.","color":"gold"}
execute unless entity @s[tag=cap_zone_inside_couronne] run title @s actionbar {"text":"Un périmètre ancien contraint vos gestes — mode aventure.","color":"dark_purple"}
