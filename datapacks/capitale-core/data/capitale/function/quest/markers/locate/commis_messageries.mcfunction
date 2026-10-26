# QMARK2 : visibilité personnelle des offres du core.
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=cap_qgiver_commis_messageries] run return run function capitale:quest/markers/at/commis_messageries with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=hcq_pnj_commis_messageries] run return run function capitale:quest/markers/at/commis_messageries with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Commis des Messageries"] run return run function capitale:quest/markers/at/commis_messageries with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Commis des messageries"] run return run function capitale:quest/markers/at/commis_messageries with storage capitale:quest_markers current
return 0
