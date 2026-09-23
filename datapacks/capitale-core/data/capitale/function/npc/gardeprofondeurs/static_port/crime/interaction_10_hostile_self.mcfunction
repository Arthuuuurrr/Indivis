function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_garde_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Avertissement inscrit. Dans les rues basses, cela se sait vite. Votre réputation rend le contrôle plus sévère.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_garde_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Avertissement inscrit. Dans les rues basses, cela se sait vite. Votre réputation rend le contrôle plus sévère ; dites l’essentiel.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_garde_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Avertissement inscrit. Dans les rues basses, cela se sait vite. Votre réputation rend le contrôle plus sévère. La garde veut des paroles nettes.","color":"white"}]
