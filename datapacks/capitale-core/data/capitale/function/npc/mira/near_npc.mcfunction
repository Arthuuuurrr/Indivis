# Near Distance alternatif si EasyNPC exécute la proximité comme le PNJ Mira.
# À mettre si le near_self joueur ne se déclenche pas.
tag @s add npc_mira_q01
execute at @s as @p[distance=..8,sort=nearest,limit=1] run function capitale:npc/mira/near_self
function capitale:npc/mira/path/q01/near_npc
