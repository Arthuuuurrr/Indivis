# Hotfix 14 near cooldown : anti-spam joueur, 120 ticks
function capitale:player/ensure_runtime_self
execute if score @s NPC_NEAR_CD matches 0 run function capitale:dialogue/random/roll_6_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_simple_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Fer ou diamant, chaque prix a sa raison. Les quais ont déjà vu assez de gens sous-équipés.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_simple_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Une bonne pièce d’armure se remarque surtout quand elle vous évite une mauvaise fin.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_simple_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Les armes brillantes attirent les regards. Les armes entretenues gardent les vivants debout.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 4 run function capitale:dialogue/sound/parole_simple_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 4 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Approchez si vous cherchez du solide. Les discours ne protègent pas des lames.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 5 run function capitale:dialogue/sound/parole_simple_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 5 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Les cargaisons du Port ne sont pas toujours paisibles. Certains préfèrent s’en souvenir trop tard.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 6 run function capitale:dialogue/sound/parole_simple_self
execute if score @s NPC_NEAR_CD matches 0 run execute if score @s CAP_DLG_RNG matches 6 run tellraw @s [{"text":"[Armurier]","color":"aqua"},{"text":" : Je vends du métal, pas du courage. Le second reste à votre charge.","color":"white"}]
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s NPC_NEAR_CD 120
