# Installation et recette de PQ01-R2

## Contenu à installer

| Composant remplacé | Fichier fourni |
| --- | --- |
| Core 1.5.7 V2 SI1 | `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R2.zip` |
| Quêtes b22 V2 SI1b | `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R2.zip` |

Les deux archives sont des datapacks complets : `pack.mcmeta` se trouve à leur racine. Le dossier `installation` du ZIP de livraison sert à ranger ces fichiers ; ce ZIP extérieur n'est pas lui-même un datapack.

1. Fermer les dialogues et sortir des relectures en cours. Sauvegarder le monde, ses données de commandes et les profils Nexus avant le remplacement.
2. Arrêter le serveur et remplacer les deux versions correspondantes dans le répertoire des datapacks du monde par celles fournies. Une seule version de chaque composant doit être chargée.
3. Conserver les JAR fournis : journal `0.1.0+1.21.11.b7-SEQUENCE-I-SI1b`, Nexus `PRE34-SI1`, Dialogue `b10-DATAPACK-COMMAND-HOTFIX`, EasyNPC et son interface `7.16.0`. Aucun JAR inchangé n'est redistribué dans ce correctif.
4. Redémarrer. Les commandes ci-dessous exigent un administrateur en jeu avec un personnage Nexus sélectionné et chargé.

## PNJ existants et hooks

R2 ne crée, ne remplace, ne déplace et ne modifie aucun modèle/skin/équipement EasyNPC. Dans l'interface EasyNPC, conserver les propriétés existantes et vérifier le nom affiché. Les quatre matelots portent seulement Gautier, Hugues, Anselme et Perrin, sans patronymes. Si un ancien nom est encore visible dans le monde, le modifier dans cette interface ; les noms des conversations sont déjà corrigés dans les fonctions.

Pour un événement d'interaction de type COMMAND, la commande doit être exécutée **comme le joueur qui interagit**, avec le niveau de permission de fonction déjà utilisé sur le serveur. Ne pas ajouter un deuxième événement si le bon hook existe. Les anciennes adresses ci-dessous sont conservées.

| PNJ | Interaction au large | Interaction au quai |
| --- | --- | --- |
| Géraud Rivet | `capitale:npc/geraud_rivet/interact_self` | Même routeur ; l’étape décide du dialogue |
| Althéon Brumeforge | `capitale:quest/spawn_dirigeable_couronne/capitaine_large/interact_self` | Même routeur, MQ03 reste prioritaire |
| Gautier | `capitale:quest/spawn_dirigeable_couronne/matelots/matelot_01_self` | `capitale:quest/spawn_dirigeable_couronne/matelots/matelot_01_quai_self` |
| Hugues | `capitale:quest/spawn_dirigeable_couronne/matelots/matelot_02_self` | `capitale:quest/spawn_dirigeable_couronne/matelots/matelot_02_quai_self` |
| Anselme | `capitale:quest/spawn_dirigeable_couronne/matelots/matelot_03_self` | `capitale:quest/spawn_dirigeable_couronne/matelots/matelot_03_quai_self` |
| Perrin | `capitale:quest/spawn_dirigeable_couronne/matelots/matelot_04_self` | `capitale:quest/spawn_dirigeable_couronne/matelots/matelot_04_quai_self` |

Dans le champ de commande, utiliser par exemple `function capitale:quest/spawn_dirigeable_couronne/matelots/matelot_01_self`. Ne pas remplacer tous les événements NBT d'un PNJ : ses near, animations, travail, équipements et actions autres qu'interaction doivent être conservés.

Near Géraud : conserver le raccordement existant vers `capitale:quest/spawn_dirigeable_couronne/geraud_large_start_self`. Near Althéon : conserver `capitale:quest/spawn_dirigeable_couronne/capitaine_large/near_self`. L'interpellation existante est appelée pour la première introduction ; son nouveau bouton « Je vous écoute. » conduit directement au premier nœud sans second clic PNJ. Une conversation PQ01 active empêche sa propre réouverture en boucle. Les paramètres near et exports NBT du monde n'ont pas été fournis : leur configuration réelle reste à contrôler dans l'interface EasyNPC.

Les commandes `pose` suivantes enregistrent **un point de waypoint à la position de l'administrateur**. Elles ne placent ni n'attachent automatiquement un PNJ. Les lancer une par une près du PNJ correspondant, sur le bon exemplaire du dirigeable, à l'emplacement réellement accessible au joueur. Ne pas exécuter les douze commandes depuis le même endroit.

```mcfunction
/function capitale:pq01/pose/geraud_large
/function capitale:pq01/pose/altheon_large
/function capitale:pq01/pose/gautier_large
/function capitale:pq01/pose/hugues_large
/function capitale:pq01/pose/anselme_large
/function capitale:pq01/pose/perrin_large
/function capitale:pq01/pose/geraud_quai
/function capitale:pq01/pose/altheon_quai
/function capitale:pq01/pose/gautier_quai
/function capitale:pq01/pose/hugues_quai
/function capitale:pq01/pose/anselme_quai
/function capitale:pq01/pose/perrin_quai
```

Pour PQ01, les points nécessaires au journal sont Géraud au large, Althéon au large et Géraud au quai. Les autres sont disponibles pour documenter les emplacements individuels. Aucun point n'est inventé à partir des anciennes coordonnées génériques. Tant que le point requis n'est pas enregistré, le waypoint correspondant reste à configurer.

Gautier reste à son poste pour l'interruption de R2. Hugues reste près des soutes/inventaires, Anselme près des instruments et Perrin à son poste de service. Ces poses/occupations se règlent dans EasyNPC sur les PNJ existants, sans modifier le navire. Le dialogue optionnel Hugues/Gautier est désactivé par défaut. Après vérification manuelle qu'ils sont chargés et peuvent se parler à portée dans le décor, l'activer avec `/data modify storage capitale:pq01_config allow_banter set value 1`. Il refuse la présence d'un autre joueur à 16 blocs et utilise un cooldown de 1 200 ticks en ligne ; il n'anime pas leurs déplacements. Pour le désactiver, remettre cette valeur à `0`.

## Rejouer uniquement PQ01

Ces fonctions existent dans le core livré : leurs chemins sont contrôlés à l'assemblage. Elles sauvegardent/restaurent les états propres à PQ01, `QUEST_PROLOGUE`, `CAP_QUETEACTIVE` et la position, avec une clé **compte Minecraft + personnage Nexus**. Elles ne réinitialisent ni les autres quêtes, ni l'inventaire, ni les rangs, ni les réputations.

```mcfunction
/function capitale:pq01/admin/test_debut
```

Cette commande sauvegarde l'état actuel une seule fois et ramène ce personnage au début historique du dirigeable, dans l'Overworld à `764 299 1101`. La fin du parcours de test donne le statut PQ01 terminé pour la recette, mais aucune récompense, aucun rang et aucun lancement de PQ02. L'amarrage utilise le TP historique `93 228 82` ; le `spawnpoint` réel n'est pas changé pendant le test.

Après la recette, fermer le dialogue et attendre la fin d'une éventuelle transition, puis :

```mcfunction
/function capitale:pq01/admin/test_fin
```

Cette commande restaure les scores sauvegardés et la position précédente, puis supprime cette sauvegarde. Après une déconnexion ou un changement de personnage, sélectionner de nouveau le même personnage pour restaurer sa propre sauvegarde. Une deuxième commande `test_debut` refuse d'écraser une sauvegarde existante.

Pour cibler un joueur, remplacer `NomMinecraft` par son pseudo réel :

```mcfunction
/execute as NomMinecraft at @s run function capitale:pq01/admin/test_debut
/execute as NomMinecraft at @s run function capitale:pq01/admin/test_fin
```

Commandes de diagnostic/récupération :

```mcfunction
/function capitale:pq01/admin/status
/function capitale:pq01/admin/fermer_dialogue
/function capitale:pq01/admin/reparer_scene_gautier
```

`reparer_scene_gautier` réarme uniquement l'introduction de Géraud tant que PQ01 est avant le quai. C'est une réparation administrateur, qui autorise volontairement la relecture de cette scène sans toucher aux récompenses ou aux autres quêtes. Elle ne rétablit pas un déplacement animé.

Le bouton de fermeture, la perte du contexte de personnage, un changement de dimension, l'éloignement à plus de 10 blocs du point d'ouverture ou le timeout ferment la session sans conclure la quête. Par défaut, le timeout est de 3 600 ticks ; il est réglable dans `capitale:pq01_config timeout_ticks` entre 200 et 12 000 ticks. Les phrases longues attendent un choix manuel, sans ancienne cascade toutes les 20 ticks.

## Voix futures

R2 ne génère et ne remplace aucun fichier audio. `docs/DIALOGUES_ORDRE_JEU.md` donne les identifiants de réplique ; `config/VOIX_ET_TEMPS.csv` donne les durées de lecture conseillées. Configurer hors dialogue le son d'un nœud, lorsque la ressource existe réellement :

```mcfunction
/data modify storage capitale:pq01_config voices.c1 set value {sound:"indivis:voix/altheon/pq01_c1",ticks:180}
```

Cet identifiant de son est un exemple à remplacer par celui de la banque audio réelle. Le fichier audio n'est pas fourni. `ticks` correspond à sa durée en ticks et bloque la progression jusqu'à sa fin ; le joueur peut toujours fermer. Sans son configuré, la lecture reste entièrement manuelle. Le nettoyage arrête uniquement le son configuré de ce nœud, dans la catégorie voix. Les anciennes ressources et les sons de transition du core restent présents. La compatibilité avec un éventuel mod de voix supplémentaire n'est pas certifiée sans ses fichiers.

## Recette en jeu à effectuer

- Nouveau personnage : réveil, interruption statique Gautier lue une fois, retour au menu de Géraud, objectif Althéon.
- Fermer après chaque réplique de Gautier puis rouvrir : reprendre après la réplique déjà affichée. Near répété sans dupliquer la session.
- Quatre matelots : toutes les questions et la fermeture. Vérifier les noms visibles et les hooks du monde.
- Althéon : « Je vous écoute. » fonctionne immédiatement ; amnésie explicite avant le menu. Relire chaque famille A–E et ses sous-questions ; Avaleiv apparaît après la mission d'Althéon, indépendamment de Hugues.
- Questions facultatives : aucun TP. Seule la conclusion complète d'Althéon lance l'amarrage hors champ ; vérifier caméra, sons et position d'arrivée.
- Géraud au quai : cinq questions réutilisables ; seule la dernière conclusion termine PQ01. En parcours normal : Couronne +2, compétences et prime conservées, PQ02 disponible et objectif Léovic. Aucun Visiteur ici ; rang 10 seulement au magistrat, aucun abaissement d'un rang élevé.
- Tester les anciens boutons, double clics, fermeture, éloignement et voix configurée. Ils ne doivent pas déclencher une autre étape ou doubler une récompense.
- Deux joueurs à 10 secondes d'écart, puis deux personnages d'un compte : états, menus et sauvegardes de relecture distincts. Tester reconnexion et changement de personnage pendant chaque introduction.
- Après PQ01 : salut bref et phrases au quai. Sur un personnage avec MQ03 active, Althéon garde le routeur MQ03 prioritaire.
- Faire la recette sur une copie du monde. Aucun de ces scénarios n'a été exécuté ici sur un serveur Minecraft réel.

## Retour arrière

1. Tant que R2 est installé, chaque personnage ayant une relecture active utilise `test_fin` ; fermer les dialogues et laisser finir tout amarrage.
2. Arrêter le serveur et remettre les deux archives d'entrée sauvegardées. Les JAR n'ayant pas changé, aucun remplacement de mod n'est nécessaire.
3. Conserver les données supplémentaires `capitale:pq01_tests`, `capitale:pq01_runtime` et `capitale:pq01_config` pour diagnostic si nécessaire ; elles ne sont plus utilisées par l'ancienne version. Ne pas effectuer de reset global des profils Nexus ou des quêtes.
4. La remise des anciens datapacks ne retire pas les récompenses normales déjà obtenues. Pour annuler aussi des effets de jeu, utiliser la sauvegarde cohérente du monde et des profils prise avant installation.
