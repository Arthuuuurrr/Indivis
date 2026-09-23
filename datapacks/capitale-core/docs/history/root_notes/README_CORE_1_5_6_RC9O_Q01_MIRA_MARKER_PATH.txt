Haute Capitale Core 1.5.6 RC9o — Q01 Mira marker/pathfinding hooks

Base : RC9n.
But : compléter la scène Q01 Mira avec les mêmes conventions que les chemins/patrouilles : markers entity taggés + fonctions de route stockées dans le datapack.

Ajouts :
- Route Mira Q01 avec 4 markers :
  wp_mira_q01_home
  wp_mira_q01_bump
  wp_mira_q01_ruelle
  wp_mira_q01_cache
- Fonctions NPC :
  capitale:npc/mira/path/q01/start_npc
  capitale:npc/mira/path/q01/dispatch_npc
  capitale:npc/mira/path/q01/next_0 à next_3
  capitale:npc/mira/path/q01/return_home_npc
  capitale:npc/mira/path/q01/stop_npc
- Fonctions admin de pose/suppression/affichage des markers :
  capitale:npc/mira/path/q01/admin/menu_self

Correction : le marchand de Q01 ne déclenche plus directement le choix final. Il passe seulement à l’état 30 et demande de retrouver Mira.

Note : le mouvement reste géré par EasyNPC ou par ses hooks de route. Le scoreboard de quête ne dépend pas de l’arrivée physique de Mira, afin d’éviter les softlocks.
