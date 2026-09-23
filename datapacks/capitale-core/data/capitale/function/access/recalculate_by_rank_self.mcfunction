function capitale:player/ensure_runtime_self
execute if score @s ACCESS_CAPITALE matches 2 run scoreboard players set @s ACCESS_CAPITALE 0
execute if score @s ACCESS_CAPITALE matches 0 run tag @s remove ACCESS_CAPITALE
execute if score @s ACCESS_QH_NORD matches 2 run scoreboard players set @s ACCESS_QH_NORD 0
execute if score @s ACCESS_QH_NORD matches 0 run tag @s remove ACCESS_QUARTIERS_HAUTS_NORD
execute if score @s ACCESS_QH_SUD matches 2 run scoreboard players set @s ACCESS_QH_SUD 0
execute if score @s ACCESS_QH_SUD matches 0 run tag @s remove ACCESS_QUARTIERS_HAUTS_SUD
execute if score @s ACCESS_PALAIS matches 2 run scoreboard players set @s ACCESS_PALAIS 0
execute if score @s ACCESS_PALAIS matches 0 run tag @s remove ACCESS_PALAIS
execute if score @s ACCESS_COEUR matches 2 run scoreboard players set @s ACCESS_COEUR 0
execute if score @s ACCESS_COEUR matches 0 run tag @s remove ACCESS_COEUR
execute if score @s ACCESS_SEUILS matches 2 run scoreboard players set @s ACCESS_SEUILS 0
execute if score @s ACCESS_SEUILS matches 0 run tag @s remove ACCESS_SEUILS_SCELLES
execute if score @s ACCESS_BANQUE matches 2 run scoreboard players set @s ACCESS_BANQUE 0
execute if score @s ACCESS_BANQUE matches 0 run tag @s remove ACCESS_BANQUE
execute if score @s ACCESS_ARCHIVES matches 2 run scoreboard players set @s ACCESS_ARCHIVES 0
execute if score @s ACCESS_ARCHIVES matches 0 run tag @s remove ACCESS_ARCHIVES
execute if score @s ACCESS_TRIBUNAL matches 2 run scoreboard players set @s ACCESS_TRIBUNAL 0
execute if score @s ACCESS_TRIBUNAL matches 0 run tag @s remove ACCESS_TRIBUNAL
execute if score @s CAP_RANGSOCIAL matches 10.. if score @s ACCESS_CAPITALE matches 0 run function capitale:access/auto/capitale_self
execute if score @s CAP_RANGSOCIAL matches 50.. if score @s ACCESS_QH_NORD matches 0 run function capitale:access/auto/quartiers_hauts_nord_self
execute if score @s CAP_RANGSOCIAL matches 50.. if score @s ACCESS_QH_SUD matches 0 run function capitale:access/auto/quartiers_hauts_sud_self
execute if score @s CAP_RANGSOCIAL matches 70.. if score @s ACCESS_PALAIS matches 0 run function capitale:access/auto/palais_self
execute if score @s CAP_RANGSOCIAL matches 90.. if score @s ACCESS_COEUR matches 0 run function capitale:access/auto/coeur_self
execute if score @s CAP_RANGSOCIAL matches 90.. if score @s ACCESS_SEUILS matches 0 run function capitale:access/auto/seuils_scelles_self
execute if score @s CAP_RANGSOCIAL matches 100.. if score @s ACCESS_CAPITALE matches 0 run function capitale:access/auto/capitale_self
execute if score @s CAP_RANGSOCIAL matches 100.. if score @s ACCESS_QH_NORD matches 0 run function capitale:access/auto/quartiers_hauts_nord_self
execute if score @s CAP_RANGSOCIAL matches 100.. if score @s ACCESS_QH_SUD matches 0 run function capitale:access/auto/quartiers_hauts_sud_self
execute if score @s CAP_RANGSOCIAL matches 100.. if score @s ACCESS_PALAIS matches 0 run function capitale:access/auto/palais_self
execute if score @s CAP_RANGSOCIAL matches 100.. if score @s ACCESS_ARCHIVES matches 0 run function capitale:access/auto/archives_self
execute if score @s CAP_RANGSOCIAL matches 100.. if score @s ACCESS_COEUR matches 0 run function capitale:access/auto/coeur_self
execute if score @s CAP_RANGSOCIAL matches 100.. if score @s ACCESS_SEUILS matches 0 run function capitale:access/auto/seuils_scelles_self
execute if score @s CAP_RANGSOCIAL matches 100.. if score @s ACCESS_BANQUE matches 0 run function capitale:access/auto/banque_self
execute if score @s CAP_RANGSOCIAL matches 100.. if score @s ACCESS_TRIBUNAL matches 0 run function capitale:access/auto/tribunal_self
function capitale:access/sync_tags_self
