Haute Capitale Core — 1.5.6 RC9t — Side BA NPC router deliveries

Base: RC9s side BA path visual anchorfix.

Objectif:
- Supprimer le besoin de boutons EasyNPC secondaires pour les livraisons Q03 chez les PNJ récurrents.
- Faire fonctionner les livraisons de Lysandre via la même fonction d'interaction principale du PNJ, selon l'objectif actif du joueur.

Modifications:
- npc/mira/interact_self route vers Q03 livraison Mira si QUEST_SIDE_BA_Q03=40 et Q01=101, sinon Q01 Mira.
- npc/elias_ferbois/interact_self route vers Q03 livraison Elias si QUEST_SIDE_BA_Q03=20, sinon Q02 Elias.
- npc/roland/interact_self route vers Q03 livraison Roland si QUEST_SIDE_BA_Q03=40 et Q01=102, sinon Q04 Roland.
- Ajout de npc/greffier_ba/interact_self pour la livraison Q03 Greffier si QUEST_SIDE_BA_Q03=30.
- Ajout de npc/habitante_vieux_passage/interact_self pour la livraison finale neutre si Q03=40 et Q01 non résolue.
- Ajout de fonctions delivery_*_npc_self sans vérification d'anchor, destinées aux interactions principales des PNJ.

Conservation:
- Les anciennes fonctions delivery_*_self avec anchors restent disponibles pour des points invisibles ou anciens boutons, mais ne sont plus nécessaires pour Elias/Mira/Roland/Greffier/Habitante si les PNJ utilisent les nouveaux interact_self.
- Les anchors Q03 restent utiles pour visualisation/setup et sécurité des fonctions anciennes.

Configuration EasyNPC recommandée:
- Elias: function capitale:npc/elias_ferbois/interact_self
- Mira: function capitale:npc/mira/interact_self
- Roland: function capitale:npc/roland/interact_self
- Lysandre: function capitale:npc/lysandre/interact_self
- Greffier BA: function capitale:npc/greffier_ba/interact_self
- Habitante vieux passage: function capitale:npc/habitante_vieux_passage/interact_self

Toutes ces fonctions doivent être Execute as User ON.
