RC9x — Q01 Mira near-trigger

Objectif : rendre Q01 RP et jouable. Le joueur ne parle plus à Mira avant le vol.
Flux : Near Distance Mira -> bousculade/vol/fuite -> marchand -> retour Mira -> choix final.

Fonctions EasyNPC principales :
- Mira interaction : function capitale:npc/mira/interact_self — Execute as User ON
- Mira near distance : function capitale:npc/mira/near_self — Execute as User ON
- Marchand Q01 : function capitale:quest/side/bas_anneaux/q01_quartier_oublies/talk_merchant_self — Execute as User ON

Le near déclenche Q01 seulement si QUEST_SIDE_BA_Q01 = 0 et si Q03 de Lysandre n’est pas active, afin de ne pas changer la branche finale de Q03.
Si la route Mira est déjà active pour un autre joueur, le second joueur rejoint directement l’étape marchand sans relancer visuellement Mira.
