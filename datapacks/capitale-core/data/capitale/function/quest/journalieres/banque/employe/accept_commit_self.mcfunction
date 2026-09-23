scoreboard players set @s QUEST_DAILY_BANQUE 10
scoreboard players set @s CAP_BANQUE_TIMER 60
scoreboard players set @s CAP_BANQUE_SCEAU 0
scoreboard players set @s CAP_DISC_CERCLE 1
scoreboard players set @s CAP_DISC_BANQUE 1
function capitale:quest/journalieres/banque/documents/give_sealed_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Employé de la Banque]","color":"yellow"},{"text":" : Portez ces documents à la Banque. Soixante secondes, cachet intact : vingt Martins. Au-delà, la Banque paiera moitié moins. Si vous rompez le sceau, elle ne paiera rien.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Journalière] Nouvelle mission — La Banque.","color":"gold"}
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Objectif mis à jour] Cherchez la Banque dans le cercle marchand et remettez-y les documents scellés.","color":"yellow"}
function capitale:dialogue/sound/reponse_attendue_self
tellraw @s [{"text":"[Option risquée] ","color":"dark_red","bold":true},{"text":"[Rompre le sceau et lire]","color":"red","bold":true,"click_event":{"action":"run_command","command":"/trigger CAP_READ_BANQUE set 1"},"hover_event":{"action":"show_text","value":"Lire le pli bancaire. La Banque considérera le sceau comme rompu."}},{"text":" — La remise sera refusée si vous faites cela.","color":"gray"}]
title @s times 5 55 15
title @s title {"text":"Nouvelle journalière","color":"gold","bold":true}
title @s subtitle {"text":"La Banque — 60 secondes","color":"white"}
execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.25 1.25
