function capitale:player/ensure_runtime_self
function capitale:dialogue/sound/parole_garde_self
tellraw @s {"text":"[Accès] Accès reconnus :","color":"gold"}
execute if score @s ACCESS_CAPITALE matches 1.. run function capitale:dialogue/sound/parole_simple_self
execute if score @s ACCESS_CAPITALE matches 1.. run tellraw @s [{"text":"- ","color":"dark_gray"},{"text":"Capitale","color":"green"}]
execute if score @s ACCESS_QH_NORD matches 1.. run function capitale:dialogue/sound/parole_simple_self
execute if score @s ACCESS_QH_NORD matches 1.. run tellraw @s [{"text":"- ","color":"dark_gray"},{"text":"Quartiers hauts nord","color":"green"}]
execute if score @s ACCESS_QH_SUD matches 1.. run function capitale:dialogue/sound/parole_simple_self
execute if score @s ACCESS_QH_SUD matches 1.. run tellraw @s [{"text":"- ","color":"dark_gray"},{"text":"Quartiers hauts sud","color":"green"}]
execute if score @s ACCESS_PALAIS matches 1.. run function capitale:dialogue/sound/parole_simple_self
execute if score @s ACCESS_PALAIS matches 1.. run tellraw @s [{"text":"- ","color":"dark_gray"},{"text":"Palais","color":"green"}]
execute if score @s ACCESS_ARCHIVES matches 1.. run function capitale:dialogue/sound/parole_simple_self
execute if score @s ACCESS_ARCHIVES matches 1.. run tellraw @s [{"text":"- ","color":"dark_gray"},{"text":"Archives","color":"green"}]
execute if score @s ACCESS_COEUR matches 1.. run function capitale:dialogue/sound/parole_simple_self
execute if score @s ACCESS_COEUR matches 1.. run tellraw @s [{"text":"- ","color":"dark_gray"},{"text":"Cœur","color":"green"}]
execute if score @s ACCESS_SEUILS matches 1.. run function capitale:dialogue/sound/parole_simple_self
execute if score @s ACCESS_SEUILS matches 1.. run tellraw @s [{"text":"- ","color":"dark_gray"},{"text":"Portails scellés","color":"green"}]
execute if score @s ACCESS_BANQUE matches 1.. run function capitale:dialogue/sound/parole_simple_self
execute if score @s ACCESS_BANQUE matches 1.. run tellraw @s [{"text":"- ","color":"dark_gray"},{"text":"Banque","color":"green"}]
execute if score @s ACCESS_TRIBUNAL matches 1.. run function capitale:dialogue/sound/parole_simple_self
execute if score @s ACCESS_TRIBUNAL matches 1.. run tellraw @s [{"text":"- ","color":"dark_gray"},{"text":"Tribunal","color":"green"}]
