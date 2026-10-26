# QMARK2 : visibilité personnelle des offres du core.
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=cap_qgiver_mira] run return run function capitale:quest/markers/at/mira with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=npc_mira_q01] run return run function capitale:quest/markers/at/mira with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=hcq_pnj_mira] run return run function capitale:quest/markers/at/mira with storage capitale:quest_markers current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Mira"] run return run function capitale:quest/markers/at/mira with storage capitale:quest_markers current
return 0
