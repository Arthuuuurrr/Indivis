function capitale:dialogue/sound/parole_garde_self
tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Menace prioritaire. Les rues basses se ferment quand le danger s’annonce.","color":"white"}]
effect give @s minecraft:glowing 5 0 true
effect give @s minecraft:slowness 4 1 true
effect give @s minecraft:weakness 5 0 true
function capitale:dialogue/sound/parole_garde_self
tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Une amende de 400 Martin d’Or est inscrite. La garde ordonne votre présentation au Tribunal.","color":"white"}]
function capitale:justice/intercept/start_30_49_gardeprofondeurs_self
