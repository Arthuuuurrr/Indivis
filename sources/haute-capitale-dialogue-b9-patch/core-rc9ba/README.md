# capitale_core 1.5.6-RC9BA

Base stricte : RC9AY FULL consolidé.

RC9BA ajoute uniquement les cinq appels `hcd_interpellation nearest_named ...` versionnés dans ce
dossier. Aucun tellraw, score, objectif, TP ou autre fonction de quête n'est remplacé.

Les contrôles d'accès ne sont pas modifiés ici : les fonctions qui produisent le flux
« suivre le garde » / « tenter un arrangement » ne sont pas présentes dans le Core fourni.
Le b9 les détecte côté client uniquement lorsque le message contient le choix `CapChoix`
correspondant, ce qui évite de brancher les dialogues d'ambiance.
