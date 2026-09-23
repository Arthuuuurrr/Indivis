# Admin joueur : tague et ancre le PNJ EasyNPC le plus proche dans un rayon de 5 blocs.
# Usage : se placer près du garde statique non-accès, puis lancer cette fonction.
execute unless entity @e[type=#capitale:static_guard_npc_candidates,distance=..5,sort=nearest,limit=1] run tellraw @s {"text":"[Capitale] Aucun PNJ EasyNPC candidat trouvé dans un rayon de 5 blocs.","color":"red"}
execute as @e[type=#capitale:static_guard_npc_candidates,distance=..5,sort=nearest,limit=1] at @s run function capitale:npc/static_guards/defend_reset/tag_and_anchor_candidate_self
execute if entity @e[type=#capitale:static_guard_npc_candidates,distance=..5,tag=cap_static_guard_resettable,sort=nearest,limit=1] run tellraw @s {"text":"[Capitale] PNJ EasyNPC le plus proche tagué cap_static_guard_resettable et ancré.","color":"green"}
