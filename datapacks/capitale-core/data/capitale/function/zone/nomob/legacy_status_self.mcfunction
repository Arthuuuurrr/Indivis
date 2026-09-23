execute if score #nomob_auto CAP_NOMOB_AUTO matches 1 run tellraw @s {"text":"[Zone anti-spawn] Fallback automatique legacy : ACTIVÉ.","color":"gold"}
execute unless score #nomob_auto CAP_NOMOB_AUTO matches 1 run tellraw @s {"text":"[Zone anti-spawn] Fallback automatique legacy : désactivé.","color":"green"}
