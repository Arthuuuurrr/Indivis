# QMARK1 : visibilité personnelle des offres du core.
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=cap_qgiver_veteran] run return run function capitale:quest/markers/at/veteran with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=hcq_pnj_veteran] run return run function capitale:quest/markers/at/veteran with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Vétéran de la Garde du Cœur"] run return run function capitale:quest/markers/at/veteran with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Vétéran de la Garde du Coeur"] run return run function capitale:quest/markers/at/veteran with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Vétéran"] run return run function capitale:quest/markers/at/veteran with storage hc_quetes:session current
return 0
