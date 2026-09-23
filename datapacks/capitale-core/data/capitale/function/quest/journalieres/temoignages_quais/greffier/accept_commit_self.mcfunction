
scoreboard players set @s QUEST_DAILY_TEMOIGNAGES_QUAIS 20
scoreboard players set @s CAP_TEMOIN_QUAI_A 0
scoreboard players set @s CAP_TEMOIN_QUAI_B 0
scoreboard players set @s CAP_TEMOIN_QUAI_C 0
scoreboard players set @s CAP_TEMOIGNAGES_COUNT 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Revenez avec deux témoignages si vous voulez aller vite. Revenez avec trois si vous préférez que mon registre contienne quelque chose d’utile.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Journalière] Nouvelle mission — Trois témoignages sur les quais.","color":"gold"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Interrogez au moins deux témoins du litige de quai.","color":"yellow"}
title @s times 5 55 15
title @s title {"text":"Nouvelle journalière","color":"gold","bold":true}
title @s subtitle {"text":"Trois témoignages sur les quais","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.25
