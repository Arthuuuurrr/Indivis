# Recette HF4 dans la carte

La mise à jour conserve la progression actuelle. Pour revoir le début sur un personnage avancé, le mode réversible reste volontaire : `/function capitale:pq01/admin/test_debut`, puis `/function capitale:pq01/admin/test_fin` à la fin. Le mode de test sauvegarde le contexte du personnage ; ne pas supprimer cette sauvegarde ni forcer les scores à la main.

1. Parler à Géraud. Vérifier que Gautier marche jusqu’à son approche, que son interruption affiche uniquement Gautier, puis que Géraud reprend avec son nom. Après la réponse de Gautier, il retourne à son poste.
2. Laisser une réplique s’achever. Vérifier l’avance automatique après lecture/voix, la conservation des vraies réponses et la possibilité de fermer. Un menu de questions doit rester ouvert jusqu’au choix du joueur. Les menus vides ne doivent pas afficher « … ».
3. Parler à Anselme : vérifier ses lignes de matelot et l’absence de progression de la quête de l’archiviste Kerrec.
4. S’approcher du Capitaine : aucune interpellation PQ01. Faire un clic droit : sa première vraie réplique s’affiche. Poser les questions dans l’ordre voulu et revenir au menu. Vérifier la réponse verte « Terminer l’entretien et lancer l’amarrage. » ; seule sa sélection conduit à la séquence de départ.
5. Hors de son dialogue, observer Perrin faire A → B puis B → A avec ses pauses. Vérifier aussi au quai si les points distincts du quai ont été configurés.
6. Fermer pendant une marche et changer de personnage : aucune scène personnelle ni choix de l’ancien personnage ne doit continuer. À deux joueurs, vérifier que chacun ne voit que ses acteurs personnels.

Les quatre trajets et la copie d’apparence ont été testés en monde isolé réel. Cette liste couvre ce qui reste à observer avec le rendu, le son et la carte utilisateur. Si un chemin est inaccessible, le diagnostic se trouve dans le log serveur ; il ne doit pas apparaître dans la bouche d’un PNJ.
