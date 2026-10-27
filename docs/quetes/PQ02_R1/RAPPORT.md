# Indivis — Rapport d’intégration PQ02 R1

Livraison du **27 octobre 2026**, Fabric/Minecraft **1.21.11**, Java **21**. Version installable destinée à la recette sur une copie du serveur. Aucun déploiement sur le serveur d’Arthur n’a été effectué.

## Résultat et périmètre

PQ02 est refondue selon le kit Work v1.2 et le script narratif v1.0 : accueil, trajet, sujets facultatifs, entretien à trois, inscription, trajet court vers l’ascenseur, séparation et retour ultérieur. Le graphe comporte **111 nœuds et 225 citations**, lus directement dans le texte fourni. Les seuls ajouts éditoriaux sont quatre libellés de navigation : Parlez-moi encore de votre expérience., Revenons au Port., Revenons aux Sentinelles., Revenons aux formalités.

Le mod compagnon `indivis_pq02` apporte l’état de scène, les acteurs personnels et l’adaptation de capture. Il utilise le moteur Dialogue b10, les outils d’acteur de HF12 et EasyNPC 7.16.0. Il est compilé en Java 21 contre les véritables classes Minecraft intermédiaires, Fabric et les JAR fournis. Les JAR du journal HF12, de Dialogue, de Nexus et de Nexus NPC sont **inchangés** et ne sont pas redistribués.

La solution privée est nécessaire : le guide mondial de l’ancien core ne peut pas conduire deux joueurs indépendamment ni s’arrêter pour les questions d’un seul. Le trajet et ses repères restent ceux de l’installation ; le déplacement est désormais du pathfinding réel sur une copie du PNJ. L’ancien guide mondial est stationné, pas utilisé comme escorte concurrente.

## Autorité des fichiers et audit préalable

Le prompt et le scénario fournis ont été lus intégralement avant les modifications, ainsi que les addenda Hugues et Althéon et les documents normatifs. Les deux datapacks de production sont conservés comme originaux et copiés en mémoire par le constructeur ; leurs SHA-256 sont vérifiés avant chaque construction.

| Entrée autoritaire | SHA-256 |
| --- | --- |
| core HF9_QMARK2 fourni | `8aff8d9c45d275b9d281f5b915baad5e88e7a6c2147bc9806b4fff3cc7a7a8c4` |
| hc_quetes HF4_QMARK2 fourni | `72aa0cf454853f65e2cad769ba200e524c711c75260fe6a1d54c148c90788ba7` |
| mod quêtes HF12 fourni | `c888eec4fa5dad35127dd5a5e4e6c49ec0f870063d429877f4b2a48a09f5bb60` |
| Dialogue b10 fourni | `a2c21f24889f96880c472d168ea55cf65e84ff154ee1bbcc22dbeaa9df1d4057` |
| Nexus PRE35-SI1-OPT1 fourni | `bdb1525d5e4448cb8127e38bdcdce063d629b7039a856bf9ea74be5ea06dea11` |

Le core conserve son format **[94,1]** ; le datapack de journal conserve le format **94**. Les racines ZIP et les métadonnées ont été vérifiées. Les fonctions de chargement, de tick, de migration, de dialogue, d’accès, de récompense, de marqueurs et de sauvegarde ont été auditées, ainsi que le bytecode des dépendances pertinentes.

Attention aux noms historiques : le suffixe d’une fonction de migration n’est pas la valeur courante du scoreboard.

| Ancien suffixe d’écriture PQ02 | Valeur canonique `hcq_pq02` |
| --- | --- |
| v0 / v1 / v2 / v10 | 0 / 10 / 20 / 30 |
| v20 : trajet vers le magistrat | 40 |
| v30 : arrivée au magistrat | 50 |
| v40 : inscrit | 60 |
| v50 : trajet M–P | 70 |
| v60 : ascenseur | 90 |
| v100 : terminé | 100 |

Le journal conserve `pq_02_registre_port`, sa source d’état, ses plages et les identifiants d’objectifs. Les textes de récompenses et les cibles personnelles ont été raccordés à ces valeurs réelles.

## Dialogues, mise en scène et escorte

L’accueil A, les conversations T, les branches M et la séparation F sont tous raccordés. Les sujets Port, anecdote et Sentinelles sont conditionnés. Une simple mention des Sentinelles ne marque pas leur découverte : le contrôle exige une Sentinelle identifiée réellement visible pour ce joueur, à portée, devant lui et sans obstacle de vue. Aucun nom de magistrat, grade de Léovic ni nouvelle géographie n’est inventé.

Le chemin conserve A→B→C→D→E→F→G→H→I→J→K→L→M, puis M→N→O→P, avec les repères optionnels historiques `h_before` et `h_after`. Les points chargés sont lus dans le monde et mis en cache ; les absences ou ambiguïtés sont refusées. Un contexte/dimension incompatible n’est pas réutilisé. Aucun déplacement du joueur ni téléportation de rattrapage du guide n’est ajouté. La copie réapparaît à la position de scène sauvegardée lors d’une reprise.

Le contrôle de marche s’arrête réellement à l’ouverture d’un menu : navigation annulée, vitesse nulle, délai suspendu. Les questions ne consomment aucun délai de parcours. L’escorte attend si le joueur s’éloigne et s’arrête de manière bornée si le chemin est impossible. Les trajets vers des points rapprochés demandent une destination de bloc exacte pour éviter l’arrêt prématuré du pathfinding standard.

Les cadrages utilisent les sessions natives de Dialogue b10 : focale Léovic, focale magistrat et ancre médiane du plan à trois. Les répliques successives d’un même locuteur sont regroupées ; les changements de locuteur se font à la demande du lecteur. Les champs « ... », les réponses fictives du protagoniste et les minuteurs de lecture ne sont pas ajoutés. **Le choix de focale et les sessions ont été testés ; la composition visible du plan à trois sur la carte réelle reste à contrôler sur client graphique.**

Les apparences sont copiées depuis le PNJ existant : `SkinData`, `ModelData`, équipement et données cosmétiques, y compris la représentation de preset utilisée par Nexus NPC. Les sources permanentes gardent leurs UUID, actions et apparences. Les copies privées ont leurs propres UUID et leurs seules actions de scène. Les règles de collision et de poussée sont limitées aux acteurs PQ02.

## Réputation, souvenir personnel et récompenses

| Décision initiale, verrouillée ensuite | Réputation Garde du Port | Impression personnelle |
| --- | --- | --- |
| A01 : accueil chaleureux | +1 | +1 |
| T04b : valoriser le travail de la garde | +1 | +1 |
| M04 : remercier Léovic | 0 | +1 |
| F06 : gratitude sincère | +1 | +1 |
| F06 : curiosité ou refus poli | 0 | 0 |
| F06 : mépris | −1 | −1 |

Le **+5 automatique est supprimé** de la voie d’achèvement. Les quatre décisions sont uniques et leurs relectures ne rejouent pas les effets. Les bornes existantes de réputation du core restent applicables. Les **108 combinaisons** donnent une variation liée à PQ02 de **−1 à +3** et une impression de **−1 à +4**. Deux bonnes impressions puis le mépris donnent +1 favorable ; trois neutres puis le mépris donnent −1 défavorable.

L’inscription attribue Visiteur/rang 10 seulement en dessous de 10 et ne rabaisse pas un rang supérieur. L’absence initiale de score est traitée comme zéro. L’inscription est idempotente.

Le don est toujours l’item réel `capitale_currency:martin_dor`, **50 unités**, protégé par `CAP_LEOVIC_GIFT`. Curiosité et refus poli permettent de différer ; le mépris ne donne pas les pièces immédiatement. L’acceptation ultérieure ne donne aucun point supplémentaire. L’ancienne voie de don automatique après quête ouvre maintenant le dialogue, sans remise directe d’argent.

Sont préservés : le wrapper CapSkills existant, ses protections `CAPSK_REWARD_REGPORT`, les **165 points d’XP vanilla**, le tirage aléatoire du Port à la fin, et le second tirage historique lié au don. Les pools aléatoires ne sont pas réécrits. Un pool peut lui-même donner des pièces : celles-ci sont distinctes du don personnel de 50. L’achèvement est refusé si les fonctions indispensables aux récompenses ne sont pas disponibles, plutôt que de marquer une récompense absente comme reçue. Les nouveaux jetons utilisent leur propre trigger `Pq02Choix` ; le dispatcher partagé des autres quêtes n’est pas remplacé.

## Isolation, sauvegarde et performances

Les vues sont indexées par compte **et** personnage Nexus. Chaque acteur porte son propriétaire et son personnage ; une session/étape/nonce incompatibles ne peuvent pas valider un choix. Les cibles du journal renvoient l’acteur du demandeur, pas le plus proche acteur d’un autre joueur. Les PNJ publics sont masqués uniquement au propriétaire de la scène puis restaurés à la clôture. Un observateur continue de voir le public.

Point de compatibilité trouvé dans le test natif : `hc_quetes:runtime/cleanup_private` supprime globalement les entités `hcm_private` du compte lors de certains renouvellements MQ. Les nouveaux acteurs utilisent donc **`hcm_pq2_actor` avec une règle de visibilité dédiée**, sans cette étiquette générale. Le nettoyage de MQ reste inchangé. Ce choix évite la suppression de l’escorte par une actualisation du journal.

Les nouveaux `CAP_PQ2_*` sont persistants selon la **véritable politique PRE35** du Nexus fourni ; les champs transitoires ne le sont pas. La position est stockée séparément par UUID de personnage avec contrôle du compte propriétaire. Les hooks `profiles/before_save_self` et `after_load_self` suspendent/nettoient la scène sans attribuer de progression. Les orphelins de scène sont retirés au chargement des entités.

Pas de recherche globale de PNJ à chaque tick, pas de téléportation permanente, pas de recréation d’acteur à chaque réplique. Les recherches de repères sont locales et liées au passage d’un point. L’observation et la mise à jour du marqueur personnel sont limitées à une fois par seconde. Le moteur de visibilité existant est réutilisé. **Aucune mesure de FPS graphique ni comparaison Spark avec l’ancien serveur n’a été réalisée.**

## PQ01 : correction Hugues strictement limitée

Les six variantes de menu sont `hugues_menu`, `hugues_36`, `hugues_38`, `hugues_40`, `hugues_42`, `hugues_44`. Dans chacune : une seule ligne d’affichage est retirée ; la seule branche `pick=6 → banter_self` devient `pick=6 → return 0`. Les autres numéros restent inchangés, notamment le congé en 7.

**12 fichiers PQ01 modifiés, tous consacrés à cette suppression.** Les accroches, les répliques, les autres réponses, les scènes, les caméras, la navigation, les fonctions de banter et le JAR HF12 sont conservés. Le diff compare chaque fichier modifié à la transformation attendue exacte ; tous les autres fichiers du répertoire PQ01 sont identiques. Les six menus et les six invocations résiduelles du choix 6 ont aussi été exécutés nativement.

## Séquence I : retour vers Althéon et Archives

Les fonctions et dialogues MQ01–MQ04, les conditions de déverrouillage et les autorisations d’Archives sont conservés octet pour octet. MQ01 reste déverrouillable après le prologue, avec Résident minimum et `CAP_POINTS_TOTAL >= 5`, sans obligation de citoyenneté ajoutée.

Chemin générique conservé : `npc/altheon_brumeforge/interact_self` → `capitaine_large/interact_self` → `pq01/open/altheon_self`, qui donne déjà priorité à MQ03 pour 10..30. **Le routeur au quai ne contenait pas cette priorité dans l’entrée fournie.** Une seule instruction identique y a été préfixée, accompagnée d’un commentaire, en conservant tout le corps original. Aucun nouvel Althéon n’est créé ni déplacé.

Le banc natif vérifie les deux entrées du capitaine, 10→20→30→40, la Sentinelle 40→50, l’accès temporaire d’Archives, puis l’Archivaire 50→100 et MQ04=10. Un second personnage à MQ03=0 n’obtient pas cette scène. La répétition de la conclusion ne rejoue pas la transition. Les données de localisation d’Archives/Havre-Fort du banc sont des données de test, pas des coordonnées installées dans la carte utilisateur.

## Vérifications exécutées

| Niveau | Résultat | Portée exacte |
| --- | --- | --- |
| ZIP/JAR, JSON, métadonnées, racines, compilation | PASS | Entrées authentifiées et compilation réelle Java 21, pas des contrats fictifs. |
| Contrôles statiques automatisés | **246 PASS** | Périmètre des diffs, conservation MQ/PQ01, états/identifiants du journal, graphes et références. |
| Java : règles pures et politique Nexus réelle | **145 PASS** | 108 combinaisons, bornes, mémoire, politique PRE35, trames de lecture, pause et échec borné du contrôleur. |
| Serveur Fabric/EasyNPC natif jetable | **1609 PASS** | Acteurs et navigation réels, trackers/paquets, sessions et mixins, deux escortes, menus, déconnexion/reprise, récompenses et chaîne MQ03. |
| Capture b10 et adaptateur client en mode sans rendu | PASS | 111 nœuds, toutes citations et choix, changements de locuteur, déclencheurs et avancement explicite du client. Écran et moteur caméra doublés. |
| Client graphique sur carte Indivis, FPS, voix enregistrées | **Non réalisé** | Carte, exports PNJ, configurations et client graphique cible non fournis au banc. |

Le serveur de test a réellement démarré et s’est arrêté sous Fabric Loader 0.19.5, Fabric API 0.141.6+1.21.11 et EasyNPC 7.16.0. Les **connexions, accusés de réception des chunks et sélections/restaurations Nexus sont synthétiques**. Les classes de règles de persistance Nexus sont celles du JAR réel. Le test A→B→A n’est donc pas présenté comme un test du sélecteur graphique Nexus ni de toute sa sérialisation disque.

Dans le banc uniquement, la monnaie est remplacée par des pépites d’or pour permettre l’exécution sans le mod de monnaie ; le test vérifie qu’il en reçoit exactement 50, puis toujours 50 après rejeu. CapSkills et les tirages du Port sont comptés par des fonctions de test. **Ces substitutions ne figurent pas dans les fichiers livrés.** Le wrapper vanilla d’XP est celui du core réel et ses 165 points sont vérifiés dans les données natives du joueur. Le cycle d’inscription, le refus méprisant, l’accueil favorable global après plusieurs décisions positives et le don ultérieur ont été exécutés.

Le serveur de test hors ligne émet une erreur de récupération des clés publiques Mojang due à son environnement réseau ; elle n’empêche pas le démarrage. Le rapport natif ne contient aucun échec de chargement de fonction, aucune exception du runtime PQ02 ni erreur de mixin. Les premiers essais ont trouvé puis fait corriger : enregistrement des permissions 1.21.11, première capture, sauvegarde d’étape, nettoyage privé par MQ, tolérance des petits trajets, maintien de session, rang absent et soustraction des scores. Le résultat livré correspond aux essais réussis suivants ; aucune ancienne trace d’échec n’est utilisée comme preuve de réussite.

## Diff et fichiers livrés

| Archive | Modifiés | Ajoutés | Supprimés | Inchangés |
| --- | --- | --- | --- | --- |
| core fourni → PQ02 R1 | 50 | 12 | 0 | 11274 |
| hc_quetes fourni → PQ02 R1 | 2 | 0 | 0 | 3321 |

`diffs.json` donne chaque chemin et sa justification. `datapacks.patch` contient le diff textuel complet. `coverage.json` relie les nœuds aux lignes du script ; `COUVERTURE.md` en donne une vue lisible. `build.json` et `SHA256SUMS.txt` identifient exactement les binaires testés. Le diff unifié a été appliqué à des copies des originaux : tous les fichiers obtenus correspondent octet pour octet aux deux archives de sortie. Les JAR de production ne contiennent aucune classe de test ; les binaires internes de la livraison sont ceux du banc réussi (`archive_qa.json`).

`INSTALLATION.md` détaille serveur/clients, liaisons, reprise, migration et retour arrière. L’archive de sources fournit les sources nouvelles, les bancs et les scripts. La reproduction demande les entrées originales nommées dans `build.py` et le script narratif du kit ; les dépendances du banc sont téléchargées depuis leurs distributions officielles ou reprises des fichiers fournis, jamais installées sur le serveur utilisateur.

## Limites et données manquantes

Il manque une copie exploitable de la carte ou les exports des PNJ et des marqueurs A–P pour certifier les obstacles, positions, comportements publics et cadrages réels. Les configurations et les fichiers audio sont nécessaires à une recette des doublages. Les implémentations tierces de monnaie/CapSkills/RPG n’ont pas été exécutées intégralement dans ce banc : leurs appels de production sont conservés. La livraison est **installable et testée dans le périmètre décrit**, pas déclarée sans risque de régression sur un modpack et une carte qui n’ont pas été lancés ici.
