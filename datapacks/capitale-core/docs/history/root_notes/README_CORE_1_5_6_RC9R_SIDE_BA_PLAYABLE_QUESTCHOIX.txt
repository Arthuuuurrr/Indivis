RC9r — Refonte jouable des quêtes secondaires Bas-Anneaux Q01-Q04

- Reprise depuis RC9q.
- Architecture alignée sur les quêtes existantes : EasyNPC appelle seulement interact_self / near, le datapack gère dialogue tellraw, QuestChoix, états, titres, récompenses et réputation.
- Ajout des fonctions manquantes quest/dialogue/* référencées par core/tick.
- Q01-Q04 n’exposent plus de fonctions d’étape dans le menu du PNJ principal.
- Les choix passent par /trigger QuestChoix avec CAP_QDIALOG_OWNER :
  Q01 owner 20, Q02 owner 21, Q03 owner 22, Q04 owner 23.
- Les étapes de monde restent verrouillées par état + anchors RC9q.
- Mira conserve le menu admin de pathfinding/markers introduit en RC9q.

EasyNPC recommandé :
- Mira : function capitale:npc/mira/interact_self
- Elias : function capitale:npc/elias_ferbois/interact_self
- Lysandre : function capitale:npc/lysandre/interact_self
- Roland : function capitale:npc/roland/interact_self
- Étapes : utiliser les fonctions step/delivery sur les bons PNJ/points, avec anchors placés via /function capitale:quest/side/bas_anneaux/setup/menu_self
