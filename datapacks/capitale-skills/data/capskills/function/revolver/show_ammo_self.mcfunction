function capskills:revolver/ammo/read_main_self
function capskills:revolver/ammo/read_off_self
execute if score @s CAPREV_STATE matches 1 run title @s actionbar [{"text":"Revolver : ","color":"gold"},{"score":{"name":"@s","objective":"CAPREV_MAIN"},"color":"yellow"},{"text":"/6","color":"gray"}]
execute if score @s CAPREV_STATE matches 2 run title @s actionbar [{"text":"Revolver : ","color":"gold"},{"score":{"name":"@s","objective":"CAPREV_OFF"},"color":"yellow"},{"text":"/6","color":"gray"}]
execute if score @s CAPREV_STATE matches 3 run title @s actionbar [{"text":"Double revolver : ","color":"gold"},{"score":{"name":"@s","objective":"CAPREV_MAIN"},"color":"yellow"},{"text":" + ","color":"gray"},{"score":{"name":"@s","objective":"CAPREV_OFF"},"color":"yellow"},{"text":" coups","color":"gray"}]
