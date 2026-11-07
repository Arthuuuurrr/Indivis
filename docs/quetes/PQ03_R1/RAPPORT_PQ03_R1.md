# PQ03 R1 — Rapport de livraison

**7 novembre 2026 — Minecraft Fabric 1.21.11.** PQ03 « Sous le regard du Cœur » est intégrée aux composants fournis, avec ses deux parcours et ses 57 blocs de dialogue. Les contrôles détaillés ci-dessous ont été exécutés sur les trois fichiers dont les empreintes figurent à la fin. La recette graphique et les mesures sur la vraie carte restent à faire.

## Base et audit avant changement

Le scénario intégral est `PQ03_finalisation_Work_2026-11-07.md`. Le core réel, le journal b22 Fusion C1, le JAR PQ02 HF9, Nexus PRE35 SI1 OPT3 et le HUD OPT1 fournis ont été identifiés avant modification. L’exécution de contrôle utilise Java 21.0.13, Fabric Loader 0.19.5, Fabric API 0.141.6, EasyNPC + Config UI 7.16 et Dialogue b10. Le mod de quêtes reste un seul JAR commun serveur/client. Aucun remplacement de Nexus, du HUD, de Dialogue ou d’EasyNPC n’est nécessaire.

Le pilote historique d’Aurèle utilisait un guide mondial à UUID fixe, des sélections de joueur le plus proche, des déplacements globaux, une validation pilotée par minuteurs et un +5 final. Une simple extension de ses menus aurait laissé ces conflits multijoueurs. R1 réutilise le pathfinding EasyNPC et les adaptateurs d’acteurs/caméra éprouvés de HF9, mais retire ce seul pilote mondial ; les waypoints, les états canoniques, les autres récompenses et les autres quêtes restent ceux des composants fournis.

Empreintes des bases :

- Core : `6ff671f7b71ff696980b1fdfa4302481dec30cac7db8eca6e0fb2c9bcd4a9aa6`.
- Journal Fusion C1 : `d9a0fb0d0aecd0eb4f9bc64f9cb7bae932ddaaa7f67273b042512c22ff9e67aa`.
- Mod PQ02 HF9 : `bce5ff70728f9e72617db734ecf83f1043fe343687a3806c3c3cd8841bdcae11`.
- Nexus fourni : `de06ecab0f5cf7ccee29043bff2ccb5fd2b7425c173da145c1f27559b49def7b`.
- HUD fourni et inchangé : `ad50aa33f2767be60ff517d7c7b965961c4392dae29a13c03b3ea5404ee6adab`.

## Parcours et dialogues

| Situation | Comportement R1 |
|---|---|
| Rencontre à A | Questions sur la surveillance et les accès ; choix commerces, Profondeurs, autres questions ou report. Aucun gain de route. |
| Commerces | Déplacement réel A–B–C–D–E–F–G–H–I. B02 à D seulement après confirmation du son réel ; B05 à G. |
| Arrivée I | Questions et au revoir B06 ; le retour ne commence qu’après le choix/accord de séparation. |
| Visite libre | Aurèle revient seul I–H–G–F–E–D–C–B–A, sans exiger la présence du joueur ; attente à A, puis B07. |
| Profondeurs | Départ direct A–J–…–V, ou même trajet après B07. Aucun passage inventé dans les commerces. Arrêts N et S. |
| Conversation pendant un segment | Vraie pause de la navigation et du timeout. Questions réentrantes B03/B04. Reprise du segment interrompu à la fermeture ou par le choix de reprise. |
| Éloignement | Attente au-delà du seuil de 18 blocs pendant l’escorte accompagnée ; indication privée espacée, aucune sélection d’un autre joueur. Le retour libre I–A est exempté. |
| Arrivée V | Immobilisation, orientation vers le joueur lors de l’entretien ; C03 regroupé en quatre répliques, questions C04, position C05, conclusion C06. |
| Oubli à V | C07 une seule fois, objectif privé de retour, marqueur conservé. Aucune caméra, téléportation ou PQ04 forcée. |
| Au revoir réellement fermé | Résultat mémorisé payé une fois, fin canonique, liberté du joueur, départ physique de l’acteur V–…–J–A ou retrait hors de sa vue. |

Toutes les répliques et les choix du dossier sont couverts par l’extraction du scénario ; seule l’expression rédactionnelle « validé » de la section suivante est exclue. Les salutations postquête sont les trois répliques déjà présentes dans le core, sélectionnées selon le souvenir. Les raccords C04 proposés dans le dossier sont inclus tels que fournis. Les phrases consécutives sont regroupées ; les réponses facultatives reviennent au menu sans rejouer l’introduction. Les choix qui engagent ou terminent un trajet sont verts. Aucun défilement automatique n’empêche la lecture des blocs.

Le premier commentaire B01 est une réplique privée pendant la marche ; les arrêts de lore D/G/N/S suspendent réellement le guide et proposent leur conversation à proximité. Le bloc B02 est désactivé par défaut car le son de la carte n’a pas été vérifié ; son activation ne fabrique aucun grondement.

## États canoniques conservés

Le suffixe d’une fonction de migration n’est pas toujours la valeur du score. Ces correspondances sont conservées ; les lecteurs et écrivains `hc_quetes:migration/*/pq03/*` restent octet pour octet ceux de Fusion C1.

| Fonction de migration | Score `hcq_pq03` | Sens R1 |
|---|---:|---|
| `v0` | 0 | Rencontre non engagée |
| `v1` | 10 | Offre et questions initiales |
| `v10` | 20 | Report sans départ |
| `v20` | 30 | Escorte commerces et entretien à I |
| `v25` | 40 | Retour privé I–A, joueur libre |
| `v30` | 50 | Aurèle attend à A ; retrouvailles |
| `v40` | 60 | Escorte Profondeurs |
| `v50` | 90 | Arrêt V et conclusion à obtenir |
| `v100` | 100 | Quête terminée |

Les nouveaux scores `CAP_PQ3_*` appartiennent à la politique persistante CAP_ de Nexus : version, page, deux décisions, impression, point, pause, récompense, clôture, rappel, grondement et branche. Le stockage de position est `capitale:pq03_scene/<UUID du personnage>` et conserve aussi l’UUID du compte, le point, l’étape et une pause de lore non encore présentée. Les jetons de choix, UI et propriétaire de fenêtre sont temporaires. Le propriétaire de fenêtre PQ03 vaut 98, distinct de celui de PQ02 (97).

## Réputation, souvenir et récompenses

| Décision exclusive | Réputation et impression |
|---|---:|
| B04 : reconnaître discipline et discernement | +1 |
| B04 : souplesse ou règlement strict, ou sujet non abordé | 0 |
| C05 : remerciement argumenté | +1 |
| C05 : neutre ou critique respectueuse | 0 |
| C05 : mépris explicite | −1 |

La plage totale est **−1 à +2**, sans bonus automatique et sans récompense pour l’itinéraire choisi. Les deux décisions sont mémorisées une fois ; le résultat est versé **à la fermeture réelle de l’au revoir C06**, conformément à la consigne de conclusion. Fermer C05, afficher C06 ou quitter V ne suffit pas. Le garde-fou exige l’état 90, une décision finale valide, l’au revoir clôturé, une récompense non déjà reçue et la dépendance de récompense présente.

Le score de faction reste `REP_GARDECOEUR` avec les bornes existantes ; l’impression privée est `CAP_PQ3_IMPR`, conservée par personnage. Les **175 XP vanilla**, compétences et prime technique aléatoire empruntent leurs fonctions existantes, préservées octet pour octet. La fin utilise `v100`, `QUEST_PROLOGUE=40` et les verrous existants. Un personnage déjà terminé n’obtient ni seconde récompense ni recalcul rétroactif de réputation.

## Isolation, sauvegarde et charge

Chaque acteur privé a un UUID aléatoire et deux identités : compte propriétaire et personnage Nexus actif. L’Aurèle public reste immobile, visible aux autres joueurs, masqué seulement pour le propriétaire qui voit sa copie. Les filtres de visibilité et de waypoint consultent ces deux identités. Les acteurs privés ne peuvent pas entrer en collision ou prendre des dégâts ; seuls les objectifs de navigation contrôlés sont actifs.

Le dialogue possède sa session native b10, son jeton de choix et son accusé de caméra. Une conversation étrangère en cours, notamment celle de Léovic, bloque l’ouverture PQ03. Les sons éventuels utilisent uniquement `@s`. La copie prend l’apparence du PNJ public et remplace seulement ses actions/IA pour la scène privée ; le PNJ public n’est pas téléporté.

Les sauvegardes périodiques Nexus conservent l’acteur et la fenêtre actifs en appelant le vrai writer. Une sauvegarde finale suspend et retire la copie de ce personnage, y compris pour un compte non opérateur, sans dépendre des permissions d’une fonction de sauvegarde. La reprise recrée au point persisté après chargement de sa zone. Les autres états de personnage ne sont pas repris comme propriété du compte.

Les scans de tous les markers chargés sont réservés aux commandes de préparation/diagnostic. Les points sont ensuite consultés localement ou dans la mémoire de configuration. Aucun chargement forcé de chunk n’est ajouté. Un point absent ou en doublon empêche l’avancement ; un prochain passage non chargé laisse Aurèle attendre. Le mouvement conserve la consigne HF9 de 1,2, la reprise bornée de navigation inactive et l’enchaînement des points ordinaires sans arrêt artificiel. Cela décrit le mécanisme ; **aucun gain de FPS ou de MSPT n’est chiffré**.

## Compatibilité et conservation

- **Core : 100 fichiers existants modifiés, 12 ajoutés, 11 240 conservés.** Les changements portent sur le pilote Aurèle, ses entrées, sa conclusion, deux appels de tick, les hooks de sauvegarde et le contrôle de guide perdu. Tous les fichiers PQ01/PQ02/MQ hors de cette liste restent identiques.
- **Journal : 2 fichiers modifiés, 3 328 conservés.** Seules les métadonnées du pack et la fiche PQ03 changent. Les 69 autres fiches, leurs conditions et les fonctions Fusion C1 restent identiques.
- **JAR : 3 entrées existantes modifiées, 31 ajoutées, 255 conservées.** Modifications : `fabric.mod.json`, les huit libellés de question dans `indivis_pq02_graph.json`, et `Pq02ProfileCheckpoint.class` pour déléguer un checkpoint réellement détenu par PQ03. L’implémentation de Léovic, ses acteurs, sa caméra, le client antérieur, les anciens mixins et PQ01/Gautier restent binaires identiques à HF9.
- **PQ02 :** les huit questions « Que vais-je trouver là-haut ? » deviennent « Que vais-je trouver au Cœur ? ». Aucun identifiant, embranchement, réponse de Léovic, récompense, parcours ou versement des 50 Martins n’est modifié.
- **Hugues :** le retrait du seul choix n°6 reste conservé ; les six menus historiques exercés gardent leurs cinq questions.
- **MQ03 :** les tests exécutent le retour au capitaine Althéon, le journal de bord, les Archives, leur accès temporaire, la fin MQ03 et l’ouverture MQ04. Aucun fichier MQ01–MQ03 n’est réécrit.
- **PQ04 :** aucune réplique ou fonction réécrite. Son marqueur devient éligible après la conclusion de PQ03 ; le démarrage de la conversation avec Roch reste indépendant.
- **Clément :** les 105 assertions C1 sont rejouées, notamment les dix Martins, insignes, refus persistant d’Aurèle, objets souvenirs et distinction clé/cage.

La liste exhaustive par fichier, avec motif et empreintes avant/après, est `files_changed.json`. Les fonctions modifiées sont détaillées dans `CHANGEMENTS_FICHIERS.md` ; les anciens chemins restent présents pour les PNJ déjà installés.

## Tests exécutés

| Vérification | Résultat | Portée réelle |
|---|---|---|
| Compilation Java 21 et intégrité ZIP/JSON | PASS | Trois composants réels ; mixins avec annotation réelle, pas de classes de test distribuées |
| Conservation et scénario | **15 194 contrôles PASS** | Comparaison octet pour octet hors périmètre, 57 blocs et couverture du dossier ; validation statique |
| Règles Java | **74 assertions PASS** | 16 combinaisons B04/C05, minima/maxima, routes exactes, vraie politique Nexus OPT3, trames et étapes |
| Serveur Fabric / EasyNPC / Dialogue | **2 086 assertions PASS** | Deux routes simultanées, pauses/questions, vrais acteurs et pathfinding sur carte synthétique ; régressions PQ01/PQ02/MQ03/C1 comprises |
| Redémarrage réel, second processus Fabric | **9 assertions PASS** | Même monde, JAR inchangé, compte non OP, profil Nexus réel au point N, état 60, page B04 et position restaurées |
| Protocole client PQ03 | **296 contrôles PASS** | 57 blocs, état b10 et événements Fabric réels, doubles pour l’écran/monde/client et l’envoi C2S |
| Protocole PQ02 avec PQ03 chargé | **566 contrôles PASS** | 111 blocs PQ02 avec les trois listeners client initialisés |
| Chargement du journal | **70 fiches, 0 erreur** | Vrai chargeur de quêtes ; 7848 fonctions de fixture incluses sans erreur de parsing |

Le scénario natif exerce notamment les reports répétés, les sujets B03/B04, le maximum +2 et minimum −1, l’absence de gain avant au revoir, le rappel unique à V, l’absence de point des commerces chez le joueur direct, le retour intégral I–A chez le joueur en visite et le départ final physique. Une pause de 140 ticks reste immobile pendant la discussion. Le contrôle de guide perdu historique est appelé 65 fois pendant l’éloignement sans réinitialiser la quête. Les relectures et les recharges ne dupliquent pas le résultat.

Les états canoniques 30/40/50/60/90 sont sauvegardés puis rechargés par le vrai writer/loader Nexus. Deux personnages d’un même compte sont sélectionnés successivement A–B–A, avec page, décisions et progression distinctes. Les paquets d’apparition et les filtres réels sont inspectés pour deux propriétaires et un observateur. Les sauvegardes périodiques conservent la page/session/token d’Aurèle et restent compatibles avec celles de Léovic.

Les nouveaux cas négatifs sont effectivement exécutés : marker requis absent, doublon, armor stand portant le même tag, cible dans un chunk non chargé, pause historique et menu réouvert après effacement du dernier texte en mémoire. Le vrai tick du core est exécuté **1 296 fois**, avec **335 intercalations narratives**. Les erreurs réseau de clés publiques Yggdrasil dans la fixture hors ligne sont identifiées ; aucune exception PQ03 ni erreur de chargement des fonctions n’est observée.

### Limites à ne pas confondre avec une recette en jeu

Le monde est synthétique : aucune carte Indivis n’a été ouverte et aucun client graphique n’a rendu les angles ou les marqueurs. Les connexions, la sélection Nexus et les accusés caméra sont synthétiques ; le writer/loader OPT3 et les fichiers de profils sont réels. La monnaie est remplacée par des pépites, les appels CapSkills/prime par des compteurs ; sept récompenses d’équipement de dépendances absentes ne sont pas exercées. L’identité/mmospawn et la permission de racine `function` sont adaptés uniquement dans la fixture ; aucun de ces substituts n’est distribué.

Le redémarrage a été exercé au point N/état 60, pas à chaque lettre. Les autres phases ont une vraie sauvegarde/recharge dans le même processus. Le changement de dimension est protégé par l’identité de monde et le chargement des positions, mais n’a pas été exercé dans un client réel. Aucun FPS, TPS, MSPT, profil Spark, son ambiant ou doublage n’a été validé. La sélection Nexus synthétique ne constitue pas un test graphique de son écran. Les 57 nœuds ont une couverture de protocole ; cela ne signifie pas que chaque permutation de dialogue a été jouée graphiquement.

## Installation et empreintes

Remplacer le **JAR de quêtes sur serveur et clients**, et les **deux datapacks sur le serveur**. Redémarrer complètement. Préparer/mémoriser une fois les points existants via la commande documentée. Les anciens placements A–V et l’Aurèle public restent utilisés. Aucun reset, aucun JAR compagnon et aucune modification de Nexus/HUD ne sont demandés.

| Composant | SHA-256 |
|---|---|
| core | `0874c5f2feda559b36ce8e365ffa3ff1be5dc1b762463a6b6e4c1bad6a45f9b0` |
| quests | `0e86afeb322ca1027ca7181e66dc35c3b85231caf1dd950569bd2942248f5276` |
| mod | `2f5ca46dd6e8dee46ead0a03b0db40065c8c68744d85c40d60403d809e0c520c` |

Les archives contiennent `INSTALLATION.md`, `COMMANDES_PQ03.md`, ce rapport, `CHANGEMENTS_FICHIERS.md`, les manifestes et les preuves automatisées. `validation.json` relie les empreintes de ces preuves à celles des composants. Les sources et la documentation sont proposées dans la PR GitHub en brouillon, sans fusion ni déploiement automatique.
