# RC9g — Near Distance dialogue marchand : Agriculteur
# À configurer dans EasyNPC en Near Distance / COMMAND / executeAsUser=true.
# Anti-spam joueur partagé : NPC_NEAR_CD = 120 ticks.
function capitale:player/ensure_runtime_self
execute if score @s NPC_NEAR_CD matches 0 run function capitale:npc/agriculteur/talk_self
execute if score @s NPC_NEAR_CD matches 0 run scoreboard players set @s NPC_NEAR_CD 120
