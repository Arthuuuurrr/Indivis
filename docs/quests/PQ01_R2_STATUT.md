# Statut PQ01

Version actuelle : **core et journal R3-HF9**, datapack hc_quetes **R3-HF4** conservé, administration alpha.3.

Les répliques consécutives du même interlocuteur sont regroupées jusqu’à une réponse du joueur, un changement de PNJ ou une transition. Les pages et blocs restent lisibles jusqu’à Espace, Entrée ou un clic ; aucune avance par minuteur. Les questions demeurent de vrais choix, la conclusion du capitaine reste verte et la transition attend la validation finale.

Géraud regarde le joueur pendant A1/A2, Gautier pendant A3 à A6, puis le joueur pour A7. Matelot et la remontrance constituent un seul bloc. Le correctif récupère une vitesse de base nulle uniquement sur l’acteur mobile temporaire ; le PNJ source est conservé. Cela ne prouve pas la cause du Perrin du monde utilisateur : ses points, son ambiance et sa vitesse réels restent à obtenir via `capitale:pq01/admin/diagnostic_perrin`, puis la ligne PERRIN_DIAG du journal serveur.

Les 92 prises conservent leurs textes, identifiants et hooks. La file de voix d’un bloc est séquentielle, attend les durées, retient les choix et annule le reste à la fermeture. Aucun nouvel audio ou JAR Indivis Voices dans HF9.

Remplacer le core sur le serveur et le journal sur le serveur et chaque client ; conserver hc_quetes HF4. Aucun PNJ, point ou liaison à replacer, aucune progression à réinitialiser.

[Causes et preuves HF9](pq01-dirigeable-r3/HOTFIX_BLOCS_ORIENTATION_R3_HF9.md) · [installation et pose](pq01-dirigeable-r3/docs/INSTALLATION_ET_POSE_PNJ.md) · [doublage par blocs](pq01-dirigeable-r3/docs/DOUBLAGE_BLOCS.md) · [livraison](pq01-dirigeable-r3/README.md).

Contrôles réussis : 1 161 de fonctions/capture et file audio ; 25 de scène dont orientation réelle ; 26 en monde Fabric ; 15 d’ambiance ; 44 client ; 191 de capture b10 intacte. Caméra, panneau et profils Nexus doublés ; aucun client graphique ni écoute définitive ou parcours SI1 du monde utilisateur. Les éléments du core hors PQ01 et hc_quetes sont conservés. PR #177 en brouillon ; aucun déploiement.
