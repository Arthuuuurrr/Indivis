# QMARK1 : visibilité personnelle des offres du core.
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=cap_qgiver_geraud] run return run function capitale:quest/markers/at/geraud with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=hcq_pnj_geraud] run return run function capitale:quest/markers/at/geraud with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Géraud Rivet"] run return run function capitale:quest/markers/at/geraud with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Géraud"] run return run function capitale:quest/markers/at/geraud with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Quartier-maître Géraud Rivet"] run return run function capitale:quest/markers/at/geraud with storage hc_quetes:session current
return 0
