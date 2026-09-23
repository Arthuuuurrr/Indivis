# Interaction fallback — Quincaillier.
# Le vrai dialogue + bouton boutique doivent être configurés dans EasyNPC.
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text": "[Quincaillier]", "color": "gray"}, {"text": " : Cordes, seaux, torches, outils simples. Rien de noble, tout d’utile.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text": "[Quincaillier]", "color": "gray"}, {"text": " : Les grands projets commencent souvent par une pelle, une échelle et quelqu’un qui a oublié les deux.", "color": "white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text": "[Quincaillier]", "color": "gray"}, {"text": " : Je vends ce qu’on regrette toujours de ne pas avoir pris avant de partir.", "color": "white"}]
