# Admin : crée/met à jour les ancres de tous les gardes statiques chargés déjà tagués cap_static_guard_resettable.
# À utiliser après placement/repositionnement des PNJ, avant d'activer largement Defend Self.
execute as @e[type=#capitale:static_guard_npc_candidates,tag=cap_static_guard_resettable] at @s run function capitale:npc/static_guards/defend_reset/set_anchor_self
tellraw @s {"text":"[Capitale] Ancres des gardes statiques EasyNPC chargés/tagués mises à jour.","color":"green"}
