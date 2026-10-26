# QMARK2 : visibilité personnelle des offres du core.
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=cap_qgiver_greffier] run return run function capitale:quest/markers/at/greffier with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=hcq_pnj_greffier] run return run function capitale:quest/markers/at/greffier with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Greffier des Quais"] run return run function capitale:quest/markers/at/greffier with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Greffier des quais"] run return run function capitale:quest/markers/at/greffier with storage capitale:quest_markers current
return 0
