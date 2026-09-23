function capitale:quest/dialogue/clear_self
function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Comme vous voudrez. Les occasions honnêtes ont parfois le tort de ne point revenir sous le même nom.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Refuser une affaire est encore une affaire. Elle rapporte seulement moins vite.","color":"white"}]
