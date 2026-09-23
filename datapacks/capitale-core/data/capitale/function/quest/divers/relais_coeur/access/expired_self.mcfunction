
function capitale:dialogue/sound/parole_garde_self
tellraw @s {"text":"[Autorisation expirée] Votre passage temporaire vers le Cœur n’est plus reconnu. Retournez voir la Technicienne du Cœur si vous devez poursuivre.","color":"red"}
execute at @s run playsound minecraft:block.beacon.deactivate master @s ~ ~ ~ 0.45 0.8
