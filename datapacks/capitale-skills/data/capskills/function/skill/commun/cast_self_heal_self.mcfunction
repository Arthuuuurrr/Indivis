# Self-heal 0.8.4 : l'accès est accepté si le joueur possède la base self-heal,
# ou une branche qui devrait logiquement l'impliquer déjà : Secours ou Magie.
function capskills:integration/ensure_current_self
scoreboard players set #allow_self CAPSK_TMP 0
execute if score @s CAPSK_SELF_UNLOCK matches 1.. run scoreboard players set #allow_self CAPSK_TMP 1
execute if score @s CAPSK_SEC_RANK matches 1.. run scoreboard players set #allow_self CAPSK_TMP 1
execute if score @s CAPSK_MAG_RANK matches 1.. run scoreboard players set #allow_self CAPSK_TMP 1
execute if score #allow_self CAPSK_TMP matches 0 run title @s actionbar {"text":"Canalisation vitale non débloquée.","color":"red"}
execute if score #allow_self CAPSK_TMP matches 1 if score @s CAPSK_SELF_CD matches 1.. run title @s actionbar {"text":"Canalisation vitale en recharge.","color":"green"}
execute if score #allow_self CAPSK_TMP matches 1 if score @s CAPSK_SELF_CD matches 0 run function capskills:skill/commun/apply_self_heal_self
