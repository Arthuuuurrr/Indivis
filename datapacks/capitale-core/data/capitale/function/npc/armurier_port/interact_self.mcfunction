# Interaction principale — Armurier du Port.
# Salutation fallback. Le dialogue et le bouton Ouvrir la boutique doivent être configurés dans EasyNPC.
function capitale:player/ensure_runtime_self
function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Vous cherchez une pièce précise, ou seulement à éviter de mourir mal habillé ?","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Le râtelier est prêt. Si vous voulez acheter, passez par le comptoir ; si vous voulez parler, je peux prendre une minute.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Fer, diamant, sangles et rivets. La Capitale use vite les imprudents.","color":"white"}]
