# Exécuté comme le joueur par le hook Nexus ; conserver le verrou présent à l'entrée.
scoreboard players set @s hcm_adm_savelock 0
execute store success score @s hcm_adm_haslock run scoreboard players get @s CAP_PROFILE_LOCK
execute if score @s hcm_adm_haslock matches 1 run scoreboard players operation @s hcm_adm_savelock = @s CAP_PROFILE_LOCK
function capitale:profiles/before_save_self
execute if score @s hcm_adm_haslock matches 1 run scoreboard players operation @s CAP_PROFILE_LOCK = @s hcm_adm_savelock
execute unless score @s hcm_adm_haslock matches 1 run scoreboard players reset @s CAP_PROFILE_LOCK
scoreboard players reset @s hcm_adm_savelock
scoreboard players reset @s hcm_adm_haslock
