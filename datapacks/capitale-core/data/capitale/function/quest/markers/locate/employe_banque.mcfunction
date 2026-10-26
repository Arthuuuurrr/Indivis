# QMARK1 : visibilité personnelle des offres du core.
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=cap_qgiver_employe_banque] run return run function capitale:quest/markers/at/employe_banque with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,tag=hcq_pnj_employe_banque] run return run function capitale:quest/markers/at/employe_banque with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Employé de la Banque"] run return run function capitale:quest/markers/at/employe_banque with storage hc_quetes:session current
execute at @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1,name="Employé de banque"] run return run function capitale:quest/markers/at/employe_banque with storage hc_quetes:session current
return 0
