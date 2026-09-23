# Rempart personnel direct, utilisé quand le joueur n'a pas encore Égide I.
function capskills:integration/ensure_current_self
execute if score @s CAPSK_REMP_SELF_CD matches 1.. run title @s actionbar {"text":"Rempart personnel en recharge.","color":"blue"}
execute if score @s CAPSK_REMP_SELF_CD matches 0 run function capskills:skill/rempart/apply_self_self
