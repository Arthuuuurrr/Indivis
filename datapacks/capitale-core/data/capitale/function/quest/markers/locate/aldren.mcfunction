# QMARK1 : visibilité personnelle des offres du core.
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=cap_qgiver_aldren] run return run function capitale:quest/markers/at/aldren with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=hcq_pnj_aldren] run return run function capitale:quest/markers/at/aldren with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Aldren le Traqueur"] run return run function capitale:quest/markers/at/aldren with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Aldren"] run return run function capitale:quest/markers/at/aldren with storage hc_quetes:session current
return 0
