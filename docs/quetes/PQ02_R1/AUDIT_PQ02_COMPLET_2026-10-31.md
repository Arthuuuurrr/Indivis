# Audit complet de PQ02 — 31 octobre 2026

## Résultat

**Les contrôles exécutés passent sur le JAR HF7 déjà livré. Aucun autre blocage n’a été reproduit dans les parcours testés.** Cet audit n’ajoute pas de version du mod : le JAR, le core et `hc_quetes` restent strictement identiques. Aucune commande de placement, liaison ou remise à zéro supplémentaire n’est nécessaire.

La conclusion porte sur les tests décrits ici. Le monde Indivis, le client graphique et le modpack complet ne sont pas chargés dans ce banc ; le cadrage visuel, les obstacles propres à la carte, les FPS et les voix restent à vérifier en jeu.

## Ce qui a été renforcé

Les anciens tests parcouraient les dialogues séparément et contrôlaient surtout l’acceptation de leurs commandes. Deux compléments ont été ajoutés :

1. **Deux parcours chronologiques simultanés**, depuis la rencontre au poste jusqu’à la fin de PQ02, puis au retour pour le don. Après leur initialisation, ils n’utilisent ni `ready()`, ni `showKey()`, ni changement forcé d’étape ou de décision. Ils avancent par les choix proposés et par la navigation EasyNPC.
2. **Contrôle de la destination des 290 transitions** : une commande acceptée ne suffit plus. Le banc exige le dialogue, l’étape ou le départ réellement attendu après chaque transition.

Le parcours chronologique active aussi le vérificateur périodique natif de `DialogueManager` et ses callbacks EasyNPC de regard. Le banc utilise les véritables sauvegarde, chargement, politique et fichiers de profils Nexus OPT3 ; seule la sélection des personnages reste simulée.

## Résultats des tests exécutés

| Contrôle | Résultat | Portée |
|---|---|---|
| Parcours chronologiques | **1 220 assertions passent** | Deux joueurs, rencontre → magistrat → ascenseur → fin → retour au poste |
| Dialogues et destinations | **2 715 assertions passent** | 111 blocs ; destination vérifiée pour chacune des 290 transitions ; régressions Hugues, Gautier et MQ03 |
| Protocole client | **566 assertions passent** | Adaptateurs PQ02, état/capture b10 et événements Fabric ; environnement graphique doublé |
| Analyse statique PQ02 | **261 contrôles passent** | Graphe, fonctions, contenu narratif, récompenses et compatibilité |
| Règles Java | **145 contrôles passent** | Décisions, combinaisons d’impression/réputation, cadence et contrat de persistance PRE35 du kit |
| Analyse du prologue | **388 contrôles passent** | 1 291 fonctions sélectionnées de PQ01–PQ05 ; références internes et dialogues de PQ01 |
| Intégrité HF7 | **Conforme** | Toutes les entrées du JAR et les deux ZIP de datapacks sont identiques à la livraison HF7 |

Les deux parcours chronologiques traversent réellement les **18 indices du parcours existant**, de A à P avec les deux guidages facultatifs de H. Ils couvrent 92 pages distinctes. Des fermetures natives puis réouvertures sont testées sur 52 pages distinctes ; le départ déjà accepté est également testé à la fermeture. La liste exacte figure dans `verification/pq02_full_audit_20261031.json` et le journal du banc.

Ces nombres ne représentent pas des essais manuels effectués sur le serveur Indivis. Les 111 blocs sont tous testés séparément ; les parcours chronologiques ne prétendent pas visiter toutes les combinaisons possibles.

## Vérifications du parcours et de ses reprises

| Étape | Vérification |
|---|---|
| Rencontre et repos | Toutes les questions d’introduction sont accessibles ; le repos ferme la scène puis le retour permet de reprendre le départ sans rejouer le gain d’accueil. |
| Départ | La fermeture native de la réponse où le départ est accepté lance bien l’escorte. Le second joueur utilise un acteur distinct. |
| Questions en chemin | Sujets conditionnés par la véritable observation des Sentinelles et par la découverte de l’anecdote. Questions et sous-questions consultées par les choix proposés. |
| Pause longue | Pendant 220 ticks réels de serveur, Léovic reste immobile et le dialogue reste ouvert, y compris avec le vérificateur périodique b10 actif. La fermeture reprend au waypoint conservé. |
| Sauvegarde périodique | Des écritures Nexus réelles conservent la scène, la session et le jeton de choix des pages où une interruption est testée. |
| Sauvegarde finale et reprise | La sauvegarde finale retire la scène. Le chargement réel depuis le fichier Nexus restaure le dialogue, la position précise de Léovic et le waypoint avant de reprendre. |
| Magistrat | Arrivée par la marche, présentation à trois, alternance des locuteurs, questions et sous-questions, inscription et séparation. Le rang monte à 10 si nécessaire ; un rang 30 est conservé. |
| Ascenseur | Les réponses après inscription déclenchent le court trajet M–P. Les questions finales reviennent au bon menu. |
| Don et fin | Les deux parcours donnent respectivement réputation/impression **+3/+4** et **−1/−1**. Le don immédiat ou l’acceptation tardive donnent exactement 50 unités de monnaie de substitution, une seule fois. |
| Refus et retour | Le personnage méprisant ne reçoit rien immédiatement. Il peut revenir, reporter encore, puis accepter sans nouveau changement de réputation ou d’impression. |
| Récompenses | Les fonctions de don et de fin rejouées n’ajoutent ni second don ni seconde récompense de compétences. Les bonus historiques restent distincts du don personnel. |
| Personnages Nexus | Sauvegarde et chargement réels A → B → A : le second personnage commence sans les décisions, le don ou l’impression du premier ; le retour restaure les valeurs du premier. Les inventaires Nexus ne sont pas simulés par ce contrôle de scores. |
| Isolation | Aucun paquet de création d’acteur privé étranger observé. Léovic public reste à son poste. Les scènes et demandes en attente sont nettoyées après clôture. |

## Suite du prologue et quête principale

La fin réelle de PQ02 laisse `hcq_pq02=100`, `QUEST_PROLOGUE=30`, sans quête active, verrou de guide ou propriétaire de dialogue. Aucun acteur privé ne remplace ensuite les waypoints du journal.

Après restauration du personnage terminé, le marqueur d’Aurèle devient éligible. Son routeur historique ouvre normalement l’offre de **PQ03**, avec `hcq_pq03=10`, `CAP_QUETEACTIVE=3` et `CAP_QSEQ=701`, sans rouvrir PQ02 ni rejouer une récompense. Les fonctions de PQ03 ne sont pas modifiées.

Le banc de régression confirme la priorité **MQ03 → Althéon au Port**, la consultation du journal de bord, l’objectif de retour, l’autorisation des Sentinelles, la remise à l’Archivaire, puis l’achèvement de MQ03 et l’ouverture de MQ04. Un autre personnage ne reçoit pas ce dialogue avant son étape. MQ01–MQ03 ne sont pas réécrites.

Le seul retrait éditorial de Hugues reste le choix 6 demandé. Gautier conserve son correctif précédent. Pour PQ04 et PQ05, cet audit vérifie les références et préserve les fichiers existants ; il ne valide pas leurs parcours physiques sur la carte réelle.

## Fichiers inchangés et installation

| Fichier installé | SHA-256 |
|---|---|
| `haute-capitale-quetes-HF12-PQ02-R1-HF7-1.21.11.jar` | `31add513a5cc3763d137156149f17abc70a73b017f94f6498b8132cdd02b067d` |
| Core PQ02 R1 HF1 de la livraison HF7 | `592ad598e9cb8164782adbfa873e325172fdb2c81458f3252de5e75f28851a9d` |
| `hc_quetes` PQ02 R1 de la livraison HF7 | `154cbdf0105296f5a94ccf6d54d7f9292c8f6fde05f9419cb7ff62225cec0fde` |

**Si HF7 est déjà installé, rien à remplacer pour cet audit.** Les PNJ, repères, commandes et progressions restent ceux préparés précédemment. L’archive d’audit contient le rapport, les preuves et les tests ; elle n’est pas un nouveau datapack ou un mod à installer. Aucune modification du serveur de production n’a été effectuée.

## Limites et recette restante

Le serveur Minecraft/Fabric, EasyNPC, sa navigation et ses actions, les sessions b10 et le rédacteur/chargeur Nexus OPT3 sont réels. Les connexions de joueurs, la sélection des personnages et l’accusé de réception de la caméra sont simulés. La navigation utilise un parcours plat de test. La monnaie utilise des pépites d’or ; CapSkills et les tirages de bonus sont comptés par des fonctions de test. Ces substitutions ne sont incluses dans aucun fichier installé.

L’essai en jeu doit encore confirmer le cadrage des dialogues à trois, l’accessibilité physique de tous les points sur la carte Indivis, le rendu des marqueurs, les doublages et les performances du modpack complet avec deux clients. L’absence d’autre défaut dans ces bancs ne constitue pas une garantie d’absence de bug dans ces éléments non testés.

## Reproduire les contrôles

Depuis les sources et dépendances documentées de PQ02 :

```bash
python tools/pq02/prepare_runtime.py
python tools/pq02/bootstrap_runtime.py
PQ02_INTEGRATED=1 PQ02_END_TO_END=1 python tools/pq02/run_native.py
PQ02_INTEGRATED=1 PQ02_STORY_AUDIT=1 python tools/pq02/run_native.py
python tools/pq02/run_protocol.py
python tools/pq02/test_static.py
python tools/pq02/audit_prologue.py
python tools/pq02/verify_pq02_full_audit.py
```

Exécuter les deux bancs serveur l’un après l’autre : ils partagent le port de test 25579. Les chemins ci-dessus sont ceux du dépôt GitHub ; dans l’archive d’audit, les sources conservent cette arborescence et complètent le projet PQ02 existant. Les dépendances propriétaires fournies et le JAR HF7 exact sont nécessaires et ne sont pas redistribués dans cette archive.
