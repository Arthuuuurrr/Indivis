# NexusCharacters PRE32 — correctif du cache des bras et vérifications

La PRE32 conserve la géométrie 3D de la PRE31 et supprime les recalculs des parties non affichées lors du rendu d'une main. Le cache du bras est indépendant du rendu du personnage complet. Les scénarios de remise à zéro du modèle et de passes successives sont reproduits dans le client et vérifiés. Les baisses de FPS propres à Lion-Port ne sont pas déclarées entièrement résolues : elles comportent aussi d'autres coûts, mesurés ci-dessous.

## Version livrée

- Fichier : `NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE32.jar`.
- Version Fabric : `1.0.0-beta.32+indivis.1.21.11`.
- Taille : 28,160,436 octets.
- SHA-256 : `203fb5e54098836c34a375723a8ce234c5aa884739f4a41113120db5a5465032`.
- Base exacte PRE31 : `d0d725337353dcc7328b86a46ace83ca82b66b7680416a8b555900de764fe203`.

Remplacer le JAR PRE31 dans le dossier `mods` du client par la PRE32. Aucun fichier de configuration ni nouveau mod n'est nécessaire. L'archive ZIP contient ce JAR et ce rapport. Le protocole réseau et le code serveur sont identiques à la PRE31.

## Ce qui change et pourquoi

En PRE31, `GenericSkinLayerSupport.firstPerson` ignorait le paramètre indiquant le bras soumis au rendu. La préparation travaillait sur les six parties du modèle. Le même état servait aux bras en première personne, au personnage dans le monde et à l'inventaire. La PRE31 évitait certaines réécritures quand la pose relative restait stable ; elle conservait les recalculs coûteux lors des invalidations vues dans les profils Spark.

La PRE32 utilise effectivement le bras soumis au rendu. Chaque modèle possède deux caches distincts, gauche et droit, de 16 préparations au maximum par bras. Les entrées distinguent exactement le template et sa forme native, le choix classique/slim, les cubes du modèle et la matrice relative de la manche. La pose du bras dans le monde ne modifie pas ses faces locales. Le mouvement d'une tête ou d'une jambe non affichée ne provoque plus leur découpe pour rendre la main.

Les remises à zéro ordinaires détachent les injections concernées et conservent les préparations des mains. Le rechargement des ressources les abandonne. Les changements de tenue, de géométrie, de modèle ou de pose relative requise invalident l'entrée concernée. Une partie de bras inhabituelle fournie par un autre renderer utilise le chemin PRE31 existant.

Le rendu complet continue d'utiliser `PosedSurfaceSupport`. Ses transformations, l'écartement des couches, les épaisseurs, les normales, les UV, les règles de priorité et les découpes aux intersections sont conservés. Aucun asset, algorithme de construction des cheveux, barbes ou vêtements, menu, catalogue, code réseau ou de sauvegarde n'a été réécrit.

## Vérifications de la géométrie et des assets

| Contrôle | Résultat |
| --- | --- |
| Audit du JAR | 5 210 entrées PRE31 identiques ; aucun fichier supprimé |
| Assets embarqués | Les 4 966 fichiers sont identiques octet par octet |
| Code existant | 57 méthodes préservées après retrait des trois raccordements explicitement autorisés |
| Géométrie complète | 320 états de modèle PRE31/PRE32 identiques dans les fichiers de comparaison |
| Nouveau rendu des bras | 55 états comparés à l'implémentation PRE31 conservée comme référence, positions/normales/UV identiques par bits flottants |
| Régressions des bras | 654 assertions : deux bras, classique/slim, animations, transformations relatives, tenue, template absent, nouveaux templates, cubes remplacés/restaurés, cache distinct et ressources |
| Menus | Les 23 captures PRE31/PRE32 sont identiques pixel par pixel ; 688 assertions sur la suite complète |
| Nouveaux PNG | Coiffure, barbe et tenue sous de nouveaux identifiants 930, détectés automatiquement ; corps 2, classique/slim, maillages complets et quatre états de bras comparés à PRE31 ; 23 assertions dédiées |

Les comparaisons des faces des bras conservent tous leurs attributs et normalisent uniquement l'ordre des faces, sans arrondir leurs coordonnées. Le chemin PRE31 servant de référence reste intact. Les nouvelles formes utilisées dans les régressions portent des géométries et objets différents ; le test de découverte charge de nouveaux PNG depuis un pack de test extérieur au JAR. Ces fichiers de test ne sont pas livrés dans le mod.

La suite complète des menus/320 états a été exécutée avant l'ajout final du repli pour un bras inconnu ; ce repli ne change pas ces chemins, confirmé par l'audit de code. Le JAR final a ensuite repassé les 654 assertions des bras, le test des nouveaux PNG et les scénarios client suivants.

## Vérifications dans le client Minecraft

Client 1.21.11, Java 21, Fabric Loader 0.19.5, Fabric API 0.141.6, 3D Skin Layers 1.10.2, Sodium 0.8.7 et ImmediatelyFast 1.14.2. La suite en monde ajoute Iris 1.10.7, Accessories 1.4.3-beta, owo 0.13.0, EasyNPC 7.12.1 et Puffish Skills 0.19.0.

Treize scènes vérifient la première et la troisième personne, l'inventaire, les mouvements, la rotation, F1, un rechargement réel des ressources, le retour à la première personne, deux mains tenant une carte et le retour au modèle complet. Deux scènes ajoutent explicitement une passe réelle de `setupAnim` avant les mains et un `clear` du modèle avant les mains. Elles reproduisent l'interférence entre les contextes et la perte du cache ; elles ne prétendent pas identifier le composant précis de ton modpack qui déclenche chaque invalidation dans le profil original.

Le JAR final passe **84 assertions sur 397 images rendues**. La PRE31 de contrôle passe **78 assertions sur 454 images rendues**. Ces nombres d'images ne constituent pas une comparaison de FPS : la charge logicielle et le nombre d'images par tick varient.

| Scène, après préparation initiale | PRE31 | PRE32 |
| --- | --- | --- |
| Première personne stable | 1 appel du traitement complet ; aucune reconstruction supplémentaire | 1 appel du bras ; aucune préparation supplémentaire |
| Modèle complet préparé avant la main | 2 reconstructions du traitement complet par image | 1 reconstruction nécessaire au personnage ; 0 préparation supplémentaire de la main |
| Modèle remis à zéro avant la main | 1 reconstruction complète par image | 0 reconstruction complète et 0 nouvelle préparation de la main |
| F1 | 0 appel des mains | 0 appel des mains |
| Deux mains avec carte | 2 appels du traitement complet | 2 appels des bras ; 0 nouvelle préparation après chauffe |

Sur la capture finale PRE32, les 268 appels des mains nécessitent seulement 3 préparations initiales et donnent 265 réutilisations. Le rechargement et le rendu de la seconde main font bien créer les états nécessaires. Les injections 3D sont présentes dans les modèles réellement soumis au rendu. Les captures sont issues du jeu, pas d'un rendu reconstruit dans un autre moteur.

Le banc client utilise le rendu logiciel dans un monde isolé. Il n'inclut pas Lion-Port, les configurations de shaders du serveur, les mods d'armure personnalisés ni tous les plugins du modpack. EasyNPC est chargé, mais cette scène ne reproduit pas la population de PNJ de Lion-Port. Les changements de personnages complets en multijoueur ne sont pas validés par cette scène : le serveur intégré peut réappliquer son propre choix ; les changements de templates/tenues/corps sont contrôlés par les régressions dédiées.

## Mesures des recalculs

Temps CPU du thread appelant, médiane de cinq séries, sur de vrais modèles et assets chargés par Minecraft. Les diagnostics optionnels sont désactivés. Les séries utilisent 10 000 appels pour le cas chaud et 32 appels par série pour les invalidations ; les mesures comprennent l'application des poses du test. Le temps CPU est distinct du temps écoulé et des FPS globaux.

| Cas | PRE31, par appel | PRE32, par appel | Allocation Java, par appel |
| --- | ---: | ---: | ---: |
| Bras stable, cache chaud | 0.003421 ms | 0.002362 ms | 0.00 → 0.00 Mio |
| Changement de pose des parties non affichées | 45.585728 ms | 0.001215 ms | 61.59 → 0.00 Mio |
| Remise à zéro du modèle de personnage | 57.006807 ms | 0.001663 ms | 75.76 → 0.00 Mio |
| Alternance de 8 poses relatives du bras | 5.615248 ms | 0.002781 ms | 8.30 → 0.00 Mio |
| Nouvelle pose relative du bras à chaque appel | 6.631945 ms | 7.210631 ms | 8.27 → 8.13 Mio |

Les gains importants portent sur les invalidations évitables et les poses déjà présentes dans le cache. Une nouvelle pose relative de la manche à chaque appel conserve une découpe réelle : aucune amélioration de ce cas n'est revendiquée, les petites différences temporelles dépendent aussi de la chauffe/JIT et de l'environnement. La PRE32 conserve cette géométrie exacte plutôt que d'arrondir ou de désactiver la 3D. Les chiffres des anciens tests sur une pose relative stable n'étaient pas suffisants pour conclure sur le problème de l'Interstice.

## Profil ajouté : Lion-Port, F1 et direction regardée

Fichier analysé : `ZNLTxh5d7W.sparkprofile`, SHA-256 `95ecf3585f8f5ae48105a955dff1b01f71763a96a91a9df72582845c124bdd92`. Il s'agit d'un profil CLIENT de la PRE31, durée 120.117 secondes, échantillonnage Java à 4 ms. Le thread de rendu contient 119988 ms échantillonnées. Le graphe vérifie la conservation des temps inclusifs, l'unicité des parents et les temps propres non négatifs.

| Poste | Temps inclusif | Part du thread de rendu |
| --- | ---: | ---: |
| Nexus, tous chemins | 71 844 ms | 59,88 % |
| Dont première personne Nexus | 55 216 ms | 46,02 % |
| Rendu du monde complet, incluant ses descendants | 31 480 ms | 26,24 % |
| Deux principaux chemins de modèles Nexus dans le monde | 13 708 ms | 11,42 % |
| Entrées Distant Horizons sur le thread de rendu | 12 428 ms | 10,36 % |
| HUD RP | 7 120 ms | 5,93 % |
| Classes d'occlusion Sodium | 228 ms | 0,19 % |
| Classes EntityCulling sur le thread de rendu | 700 ms | 0,58 % |

Ces lignes sont imbriquées et ne s'additionnent pas. Les pourcentages ne sont ni un taux de CPU global ni un nombre de FPS. Les entrées EasyNPC explicites ne couvrent pas toutes les méthodes Minecraft héritées par ses PNJ.

Distant Horizons contient surtout le rendu des LOD (11 340 ms), dont environ 3 912 ms sous les appels OpenGL de dessin, le rendu des nuages/objets génériques et des transferts de buffers. Des appels graphiques peuvent attendre le pilote ou le GPU ; ce profil Java ne mesure pas séparément la durée GPU. Le HUD conserve également des recherches réflexives répétées déjà relevées dans l'Interstice.

Le coût CPU du calcul d'occlusion de Sodium est faible dans ce relevé. Cela ne prouve pas que tous les objets masqués sont correctement exclus ni que la carte graphique n'est pas chargée : un défaut de visibilité peut coûter ailleurs, dans le dessin. La variation selon la direction est compatible avec le nombre de personnages/objets visibles et la charge des LOD, mais le relevé ne permet pas de choisir cette cause avec certitude. Il ne repère pas les instants F1 et ne fournit pas une série de FPS/GPU par orientation.

La PRE32 traite le coût des mains et leur interférence avec le personnage complet. Elle ne modifie pas l'occlusion des chunks ou les réglages Distant Horizons. Le diagnostic de Lion-Port reste donc une mesure de la PRE31 : la disparition complète des baisses selon la direction n'est pas présentée comme vérifiée. Une capture comparable sur la PRE32, caméra fixe par séquence et F1 repéré, permettra d'évaluer ce qui reste après le retrait du coût des mains.

## Traçabilité

Les sources du correctif, les raccordements de bytecode, le script d'assemblage déterministe et les preuves numériques sont conservés dans le dossier de référence PRE32. La reconstruction vérifie les empreintes d'entrée, des classes modifiées et du JAR résultant. Les paquets téléchargeables contiennent le JAR effectivement vérifié, sans les mods de test.
