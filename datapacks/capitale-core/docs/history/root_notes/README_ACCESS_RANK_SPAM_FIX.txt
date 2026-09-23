Haute Capitale — ACCESS RANK SPAM FIX

Cause :
- Nexus appelle capitale:profiles/before_save_self périodiquement.
- pre_save_runtime_flush_self appelait access/recalculate_by_rank_self.
- Les accès automatiques (valeur 2) sont remis à 0 puis recalculés.
- La voie auto notifiée renvoie donc "[Accès] ... reconnu par votre rang." à chaque sauvegarde.

Correctif :
- ajout de access/recalculate_by_rank_silent_self ;
- before_save et resync_after_load utilisent cette voie silencieuse ;
- les vrais changements de rang/quetes gardent recalculate_by_rank_self et donc la notification ;
- ajout des fonctions access/auto et access/auto_silent manquantes dans le ZIP fourni.

Aucune règle d'accès n'est modifiée :
0 = absent, 1 = accès explicite, 2 = accès dérivé du rang.
