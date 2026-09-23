function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[La Banque] Le registre est clos. Remettez tout de même les documents : la récompense sera réduite.","color":"yellow"}
title @s times 5 45 10
title @s title {"text":"Délai dépassé","color":"red","bold":true}
title @s subtitle {"text":"Remise encore possible — 10 Martins","color":"white"}
