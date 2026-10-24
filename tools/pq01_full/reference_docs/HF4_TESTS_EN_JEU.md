# Rejouer PQ01 et effectuer la recette — HF4

## Relecture personnelle sans récompenses

Sur une copie du monde et avec le personnage voulu chargé, hors dialogue et hors transition :

```mcfunction
/function capitale:pq01/admin/test_debut
```

L’état antérieur de PQ01, ses drapeaux de dialogue, `QUEST_PROLOGUE`, `CAP_QUETEACTIVE` et la position sont sauvegardés une seule fois pour le couple compte Minecraft/personnage Nexus. Le personnage est replacé au début historique `764 299 1101` dans l’Overworld. La relecture ne réinitialise aucune autre quête, inventaire, réputation ou rang. Elle ne distribue aucune récompense, ne lance pas PQ02 et ne modifie pas le spawnpoint réel. Elle réutilise le TP historique d’amarrage `93 228 82`.

Après fermeture du dialogue et fin d’une éventuelle transition :

```mcfunction
/function capitale:pq01/admin/test_fin
```

Cette commande restaure uniquement la sauvegarde du personnage actif et sa position. Elle ne remet pas les inventaires ou le monde entier dans leur état précédent ; ne pas effectuer d’autres activités pendant une recette dont on souhaite retrouver exactement le contexte. Relancer `test_debut` en mode test ne remplace pas la sauvegarde initiale. Répéter l’opération séparément sur chaque personnage ayant un test ouvert.

## Parcours fonctionnel

1. Nouveau personnage/relecture : near de Géraud, bouton « Je vous écoute » sans second clic PNJ, introduction, Gautier quitte son poste, rejoint Géraud, s’arrête et lui fait face. Fermer puis rouvrir après chacune de ses deux répliques : les répliques déjà affichées ne doivent pas être répétées. Une reprise pendant la remontrance rejoint d’abord la position d’échange si nécessaire.
2. Après sa réponse, Gautier marche à son poste ; Géraud revient vers le joueur. Vérifier le menu répétable de Géraud et l’objectif vers Althéon.
3. Gautier, Hugues, Anselme, Perrin : chaque question et sous-question est cliquable ; retour au menu et fermeture possibles. Observer leurs occupations et leur suspension pendant leur dialogue. Aucun item n’est transféré par les gestes d’Hugues.
4. Althéon au clic droit, sans interpellation automatique : introduction une fois, amnésie canonique explicitement posée avant la conclusion. Toutes les familles A–E et sous-questions restent répétables. Avaleiv se déverrouille après la mission décrite par Althéon, indépendamment des discussions avec les matelots.
5. Une question facultative ne déclenche aucun TP. Seule la conclusion verte, explicitement marquée « Terminer l’entretien et lancer l’amarrage. », d’Althéon lance l’amarrage hors champ : sons/effets historiques, TP au navire amarré et objectif Géraud. Aucun nouveau dialogue de manœuvre.
6. Géraud au quai : cinq questions réutilisables ; seule la conclusion finale complète PQ01. Les autres membres d’équipage ont leurs réponses d’après-amarrage. Althéon garde la priorité MQ03 si elle est active.

En parcours normal, contrôler séparément sur un personnage de test approprié : Couronne +2, prime et compétences conservées, PQ01 terminé une seule fois, PQ02 disponible et objectif Léovic. Le mode de relecture supprime volontairement ces effets. Aucun rang 10 n’est donné par PQ01 ; au magistrat après Léovic, Visiteur 10 reste attribué. Un personnage de rang plus élevé ne doit pas être rétrogradé. Les fonctions du magistrat sont inchangées.

## Incidents, caméra et multijoueur

| Scénario à jouer | Résultat attendu |
| --- | --- |
| Joueurs A et B, déclenchements à dix secondes d’écart | Deux scènes personnelles ; le Gautier de B ne suit pas la progression de A ; aucun dialogue, choix, marqueur ou récompense croisé. |
| A traverse l’emplacement du Gautier de B | Aucun blocage/poussée par cet acteur invisible ; aucun effet sur une porte, une plaque ou la ronde de B. |
| Deux personnages du même compte | Reprises, drapeaux, récompenses et sauvegardes de relecture distincts. Changer pendant l’aller et pendant le retour. |
| Déconnexion pendant aller/remontrance/retour | Aucun acteur durable ou errant ; retour possible au stade de dialogue enregistré. |
| Fermer la caméra ou le menu pendant la marche | Navigation annulée et acteurs supprimés ; aucune fin de quête ou téléportation déclenchée par la fermeture. |
| Un autre dialogue remplace la caméra | Le nettoyage PQ01 ne ferme pas la nouvelle session appartenant à l’autre quête. |
| Porte fermée, escalier impraticable, chemin coupé | Arrêt/repli avec raison, sans déplacer l’original ni bloquer l’objectif Althéon. |
| Acteur supprimé/chunk déchargé | Annulation ou repli ; aucun clone abandonné. Après redémarrage, acteurs orphelins retirés à leur chargement. |
| Boutons anciens, doubles clics, durée audio active | Jeton/contexte contrôlé ; pas de double transition ni de double récompense. Le bouton fermer reste disponible en marche. |
| Éloignement, timeout, changement de dimension | Nettoyage sans progression involontaire ; reprise au bon stade. |
| Deux rondes de Perrin et échange Hugues/Gautier | Rondes indépendantes ; ambiance refusée près d’un autre joueur ; scène obligatoire prioritaire. |
| Navire amarré | Aucune destination du navire au large réutilisée au quai. Les points suffixés `_quai` sont indépendants. |

Le timeout de dialogue est de 3 600 ticks par défaut, configurable dans `capitale:pq01_config timeout_ticks` entre 200 et 12 000. La distance maximale au point d’ouverture est de dix blocs. Les pages linéaires avancent après lecture, dernière page affichée et fin de voix ; les vraies réponses et les questions attendent le choix du joueur.

## Futurs doublages

Les nœuds sont listés dans `DIALOGUES_ORDRE_JEU.md`, les intentions dans `DIRECTION_ACTEUR.md`, les durées de lecture suggérées dans `config/VOIX_ET_TEMPS.csv`. Aucun son nouveau n’est généré ou livré. Une fois la ressource audio réelle disponible, régler son nœud, par exemple :

```mcfunction
/data modify storage capitale:pq01_config voices.c1 set value {sound:"indivis:voix/altheon/pq01_c1",ticks:180}
```

Remplacer cet identifiant d’exemple par le son réel. La durée `ticks` bloque le choix suivant jusqu’à la fin audio ; fermer reste possible. Sans son configuré, les pages linéaires avancent après leur temps de lecture ; les questions et réponses restent manuelles. Les sons d’amarrage historiques sont conservés. Indivis Voices 0.2.3 est facultatif ; sa lecture active bloque aussi l’avance automatique.

## Retour arrière

Tant que R3 est présent, terminer chaque relecture avec `test_fin`, fermer les scènes et exécuter `nettoyer_scenes`. Si l’on souhaite aussi restaurer les hooks/noms ajoutés aux PNJ, libérer les liaisons individuelles avant désinstallation ; les positions d’édition ne sont pas restaurées automatiquement. Arrêter ensuite le serveur et remettre ensemble les deux datapacks et le journal précédents sur serveur/clients. Les stockages supplémentaires peuvent rester pour diagnostic ; aucun reset global Nexus n’est requis. Les récompenses normales déjà obtenues ne sont pas annulées par un retour de fichiers : pour annuler les effets de jeu, utiliser la sauvegarde cohérente monde/profils.

**Cette recette du modpack et de la carte reste à exécuter.** Un monde Fabric/EasyNPC isolé a validé la création/copie d’acteurs et quatre trajets réels ; il ne valide pas le rendu, le son ni les profils du monde utilisateur.

## Points à revoir après HF3

La mise à jour conserve la progression actuelle. Pour revoir le début sur un personnage avancé, le mode réversible reste volontaire : `/function capitale:pq01/admin/test_debut`, puis `/function capitale:pq01/admin/test_fin` à la fin. Le mode de test sauvegarde le contexte du personnage ; ne pas supprimer cette sauvegarde ni forcer les scores à la main.

1. Parler à Géraud. Vérifier que Gautier marche jusqu’à son approche, que son interruption affiche uniquement Gautier, puis que Géraud reprend avec son nom. Après la réponse de Gautier, il retourne à son poste.
2. Laisser une réplique s’achever. Vérifier l’avance automatique après lecture/voix, la conservation des vraies réponses et la possibilité de fermer. Un menu de questions doit rester ouvert jusqu’au choix du joueur. Les menus vides ne doivent pas afficher « … ».
3. Parler à Anselme : vérifier ses lignes de matelot et l’absence de progression de la quête de l’archiviste Kerrec.
4. S’approcher du Capitaine : aucune interpellation PQ01. Faire un clic droit : sa première vraie réplique s’affiche. Poser les questions dans l’ordre voulu et revenir au menu. Vérifier la réponse verte « Terminer l’entretien et lancer l’amarrage. » ; seule sa sélection conduit à la séquence de départ.
5. Hors de son dialogue, observer Perrin faire A → B puis B → A avec ses pauses. Vérifier aussi au quai si les points distincts du quai ont été configurés.
6. Fermer pendant une marche et changer de personnage : aucune scène personnelle ni choix de l’ancien personnage ne doit continuer. À deux joueurs, vérifier que chacun ne voit que ses acteurs personnels.

Les quatre trajets et la copie d’apparence ont été testés en monde isolé réel. Cette liste couvre ce qui reste à observer avec le rendu, le son et la carte utilisateur. Si un chemin est inaccessible, le diagnostic se trouve dans le log serveur ; il ne doit pas apparaître dans la bouche d’un PNJ.
