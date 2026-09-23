function capskills:revolver/ammo/read_main_self
function capskills:revolver/ammo/read_off_self
tellraw @s [{"text":"Munitions revolver — principale : ","color":"gold"},{"score":{"name":"@s","objective":"CAPREV_MAIN"},"color":"yellow"},{"text":"/6 | secondaire : ","color":"gray"},{"score":{"name":"@s","objective":"CAPREV_OFF"},"color":"yellow"},{"text":"/6","color":"gray"}]
