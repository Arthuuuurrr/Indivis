RC9p — Side Bas-Anneaux EasyNPC-safe flow

Base: RC9o.

Corrections:
- Séparation nette entre talk/interact ambiant et entrée de quête.
- Ajout de quest_self/work_offer_self sur Mira, Elias, Lysandre, Roland.
- Suppression des boutons cliquables de progression dans les tellraw des quêtes SIDE_BA Q01-Q04.
- Les étapes doivent être validées via les bons PNJ/points EasyNPC.
- Ajout/restauration des titles de début de quête sur Q01-Q04.
- Q03: livraison finale verrouillée selon le résultat de Q01 (Mira/Roland/neutre).
- Q04: choix final à brancher sur Roland ou pickpocket via boutons EasyNPC, pas via chat.

Configuration générale:
- Fonctions talk_self/interact_self: dialogue ambiant / réaction, pas de progression majeure.
- Fonctions quest_self ou work_offer_self: entrée/rappel/complétion de quête.
- Fonctions step/delivery/choice: uniquement sur le bon PNJ/point EasyNPC.
