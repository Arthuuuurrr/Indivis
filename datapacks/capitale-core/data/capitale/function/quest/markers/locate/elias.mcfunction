# QMARK2 : visibilité personnelle des offres du core.
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=cap_qgiver_elias] run return run function capitale:quest/markers/at/elias with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=hcq_pnj_elias_ferbois] run return run function capitale:quest/markers/at/elias with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Elias Ferbois"] run return run function capitale:quest/markers/at/elias with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Élias Ferbois"] run return run function capitale:quest/markers/at/elias with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Elias"] run return run function capitale:quest/markers/at/elias with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Élias"] run return run function capitale:quest/markers/at/elias with storage capitale:quest_markers current
return 0
