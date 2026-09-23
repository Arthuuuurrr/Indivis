# Répliques d'ambiance — Armurier du Port. Ne renvoie plus de boutons chat.
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_4_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Les gardes préfèrent les pièces sobres. Les nobles, eux, veulent parfois briller avant même d’avoir combattu.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Une bonne armure ne rend pas courageux. Elle laisse juste le temps de le devenir.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Depuis que les expéditions reprennent, les bottes partent presque aussi vite que les plastrons.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 4 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 4 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Les meilleurs clients reviennent avec des bosses sur le métal, pas avec des excuses.","color":"white"}]
