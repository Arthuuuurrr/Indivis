# Hotfix 14 near cooldown : anti-spam joueur, 120 ticks
function capitale:player/ensure_runtime_self
execute if score @s NPC_NEAR_CD matches 0 run function capitale:dialogue/random/roll_2_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Vous n’êtes pas encore du registre des Résidents. Revenez quand la Capitale saura sous quel nom vous appeler.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Pas encore Résident ? Alors pas encore de mission de ma part. Faites inscrire votre nom, puis revenez.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s NPC_NEAR_CD 120
