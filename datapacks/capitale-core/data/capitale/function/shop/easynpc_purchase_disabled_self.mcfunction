
function capitale:player/ensure_runtime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s [{"text":"[Comptoir]","color":"aqua"},{"text":" : Les achats par menu texte sont désactivés. Utilisez l’interface d’achat du PNJ.","color":"white"}]
tellraw @s [{"text":"Note admin : ","color":"dark_gray"},{"text":"configurer l’action EasyNPC Open Trading Menu sur ce PNJ. Le datapack ne sert ici qu’au dialogue et aux fallbacks.","color":"gray"}]
