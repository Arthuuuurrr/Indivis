# PQ03 R1 — Commandes et repères

Les fonctions s’exécutent **en jeu avec un compte administrateur** dans la dimension du parcours. Depuis la console, cibler explicitement le compte : `execute as Pseudo at @s run function capitale:pq03/admin/diagnostic`. Remplacer `Pseudo` par le nom exact. Les commandes administratives ne font pas progresser la quête et ne donnent pas de récompenses.

| Commande | Usage et conditions |
|---|---|
| `/function capitale:pq03/admin/preparer_parcours` | Mémoriser les markers A–V chargés. Refuser les doublons. Relancer après avoir chargé une autre portion. Aucun waypoint déplacé. |
| `/function capitale:pq03/admin/diagnostic_points` | Afficher les coordonnées et l’état chargé/mémorisé/absent de chaque vrai marker, ainsi que les anciens guides chargés. Lecture seule. |
| `/function capitale:pq03/admin/diagnostic` | État du personnage Nexus actif, progression PQ02/PQ03, point, pause, décisions, récompense, état de la scène et anomalie éventuelle. |
| `/function capitale:pq03/liaison/aurele` | Mémoriser le PNJ public existant situé à moins de quatre blocs. Refuser une proximité ambiguë ou une liaison pendant une scène active. |
| `/function capitale:pq03/admin/afficher_points` | Visualisation historique des waypoints ; les armor stands de débogage ne deviennent pas des cibles de navigation. |
| `/function capitale:pq03/admin/masquer_points` | Nettoyer seulement cette visualisation avec la fonction existante. |
| `/function capitale:pq03/admin/confirmer_grondement` | Autoriser B02 lors d’une prochaine arrivée à D, après avoir vérifié le son réel sur la carte. Ne joue aucun son à la place du monde. |
| `/function capitale:pq03/admin/desactiver_grondement` | Désactiver B02 pour les passages suivants. Aucun reset de quête. |
| `/function capitale:pq03/admin/nettoyer_scenes` | Suspendre/nettoyer seulement la scène du compte exécutant, en conservant son personnage et sa progression. Une scène qui marchait est recréée quand sa zone est chargée ; une conversation interrompue se reprend par interaction. |

## Waypoints existants

Les vrais points sont des entités `minecraft:marker` portant les tags **`wp_aurele_coeur_a`** à **`wp_aurele_coeur_v`**. Les lettres du document correspondent à ces suffixes, en minuscules. Les fonctions historiques sous `capitale:quest/sous_le_regard_du_coeur/escort/waypoints/` et les outils de visualisation sont conservés octet pour octet. Aucun point supplémentaire de PQ02 ou position de l’administrateur n’est utilisé.

| Trajet | Points |
|---|---|
| Visite facultative des commerces | A–B–C–D–E–F–G–H–I |
| Retour privé au poste pendant la visite du joueur | I–H–G–F–E–D–C–B–A |
| Départ direct ou après les retrouvailles | A–J–K–L–M–N–O–P–Q–R–S–T–U–V |
| Départ d’Aurèle après la conclusion | V–U–T–S–R–Q–P–O–N–M–L–K–J–A |

## Identifiants historiques

Les anciens chemins `npc/aurele/*`, `quest/sous_le_regard_du_coeur/aurele/*` restent présents et entrent dans la conversation privée du personnage. Les anciens minuteurs, téléportations du guide mondial et validations collectives sont neutralisés ; ne pas les utiliser pour forcer un état intermédiaire. L’ancien `escort/stop_self` devient une vraie pause privée, reprenable en parlant à Aurèle.

`capitale:pq03/open_self` est l’entrée de dialogue après PQ02 ; elle n’est pas une commande de validation ni de pose d’acteur. Ne pas modifier manuellement `hcq_pq03`, les scores `CAP_PQ3_*`, `CAP_PROFILE_LOCK` ou les jetons de dialogue pour effectuer la recette : les sélections normales couvrent les deux routes.

## Doublage ultérieur

Les blocs ont des identifiants stables dans `indivis_pq03_graph.json`. Le stockage `capitale:pq03_setup` peut recevoir une table `voices.<identifiant>` avec `sound` (identifiant Minecraft existant dans le pack de ressources) et `ticks` (1 à 12 000). Les sons sont envoyés uniquement au propriétaire sur la catégorie `voice` ; le choix suivant attend la durée configurée. Les menus qui réaffichent la dernière réplique ne rejouent pas sa voix. Aucun enregistrement, pack audio ou doublage n’est fourni dans R1.
