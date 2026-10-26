# QMARK1 : visibilité personnelle des offres du core.
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=cap_qgiver_technicienne] run return run function capitale:quest/markers/at/technicienne with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=hcq_pnj_technicienne] run return run function capitale:quest/markers/at/technicienne with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Technicienne du Cœur"] run return run function capitale:quest/markers/at/technicienne with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Technicienne du Coeur"] run return run function capitale:quest/markers/at/technicienne with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Technicienne"] run return run function capitale:quest/markers/at/technicienne with storage hc_quetes:session current
return 0
