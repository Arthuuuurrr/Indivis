# Perte du guide pendant 60 secondes : reset permissif vers l’objectif précédent.
function capitale:dialogue/sound/reponse_attendue_self
title @s title {"text":"Guide perdu","color":"red","bold":true}
title @s subtitle {"text":"L’escorte reprend depuis son point de départ.","color":"gold"}
function capitale:quest/guidage/reset_active_self
