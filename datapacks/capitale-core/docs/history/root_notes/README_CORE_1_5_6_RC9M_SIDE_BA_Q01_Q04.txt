Capitale Core 1.5.6 RC9m — quêtes secondaires Bas-Anneaux Q01-Q04

Base : RC9l lazy QUEST init full audit.
Ajout : premier lot indépendant des quêtes proposées par Clément, avec nomenclature stable QUEST_SIDE_BA_Qxx.

Quêtes ajoutées :
- QUEST_SIDE_BA_Q01 — Le Quartier des Oubliés, PNJ Mira, choix aider/dénoncer.
- QUEST_SIDE_BA_Q02 — Le Marteau d’Elias, PNJ Elias Ferbois, préambule artisanat/crafting.
- QUEST_SIDE_BA_Q03 — Le Marché des Mille Voix, PNJ Lysandre, livraisons et variations selon Mira.
- QUEST_SIDE_BA_Q04 — La Patrouille, PNJ Roland, choix strict/indulgent/équilibré.

Réputations ajoutées :
- REP_BAS_ANNEAUX
- REP_BA_GARDES
- REP_BA_ARTISANS
- REP_BA_MONDE_GRIS

Architecture :
- Objectifs déclarés dans core/load.mcfunction.
- Pas d'initialisation globale joueur.
- Lazy init locale dans chaque point d'entrée de quête.
- Reset admin étendu aux nouvelles quêtes et réputations.

Fonction de test :
/function capitale:quest/side/bas_anneaux/menu_self

Fonctions EasyNPC principales :
/function capitale:npc/mira/interact_self
/function capitale:npc/elias_ferbois/interact_self
/function capitale:npc/lysandre/interact_self
/function capitale:npc/roland/interact_self

Notes :
- Les boutons de progression permettent de tester le flux sans placer encore tous les points/PNJ.
- Les étapes peuvent ensuite être reliées à des PNJ EasyNPC, zones, boutons invisibles ou interactions dédiées.
- Elias pose seulement le marqueur de quête pour un raccord futur à l'arbre CapSkills crafting.
