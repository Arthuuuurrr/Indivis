function capitale:dialogue/random/roll_3_self
function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Passage éclairé]","color":"green"},{"text":" : La lampe veille de nouveau. Le passage paraît moins hostile, sans devenir tout à fait accueillant.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Passage éclairé]","color":"green"},{"text":" : La lumière tient mieux. Dans les Profondeurs, c’est déjà une victoire honnête.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Passage éclairé]","color":"green"},{"text":" : Les ombres n’ont pas disparu, mais elles semblent moins promptes à avaler les marches.","color":"white"}]
