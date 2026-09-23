function capitale:dialogue/sound/parole_garde_self
tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Vous êtes recherché. Ne comptez pas sur les ruelles pour vous couvrir. Votre réputation rend le contrôle plus sévère.","color":"white"}]
effect give @s minecraft:glowing 5 0 true
effect give @s minecraft:slowness 4 1 true
function capitale:dialogue/sound/parole_garde_self
tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Une amende de 200 Martin d’Or est inscrite. Cette comparution ne sera pas différée sans justification solide.","color":"white"}]
function capitale:justice/intercept/start_30_49_gardeprofondeurs_self
