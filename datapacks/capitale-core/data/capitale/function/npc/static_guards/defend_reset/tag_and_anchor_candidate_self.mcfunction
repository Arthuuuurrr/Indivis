# Marque l'entité EasyNPC courante comme garde statique resettable et crée/met à jour son ancre.
# À appeler en contexte PNJ depuis les fonctions admin nearest/bulk.
tag @s add cap_static_guard_resettable
function capitale:npc/static_guards/defend_reset/set_anchor_self
particle minecraft:happy_villager ~ ~1.8 ~ 0.25 0.25 0.25 0.01 5 force
