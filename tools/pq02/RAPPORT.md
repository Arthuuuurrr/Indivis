# PQ02 R1 HF8 — rapport de correction et de validation

Livraison du 1er novembre 2026, Fabric/Minecraft 1.21.11, Java 21. Version installable destinée à la recette sur le serveur ; aucun déploiement en production ni essai graphique sur la carte Indivis effectué ici.

## Règle validée avec l’utilisateur

Le kit Work v1.2 et le script intégral v1.0 fournis ont été lus. Ils contenaient encore le don refusable/reportable. La précision du 1er novembre prévaut : **les 50 Martins d’Or sont versés systématiquement pendant la scène F06, avant la réponse, une seule fois par personnage**. Aucun retour auprès du Léovic permanent n’est requis.

Les quatre réponses sont conservées : curiosité (0/0), embarras (0/0), reconnaissance (+1/+1) et « Je ne demandais pas votre charité. » (−1/−1). Les pièces restent acquises dans les quatre cas. La formulation embarrassée et la réponse correspondante de Léovic ont été adaptées pour retirer le refus ; ces adaptations sont identifiées dans le script de travail v1.1 et `editorial_hf8.json`. Le script v1.0 fourni n’est pas écrasé.

## Changement de déroulement

Après l’inscription et la marche M–P, les questions de F restent disponibles. Entrer dans F06 remet la bourse et présente A/B/C/D. Chaque réponse mène à sa réplique de Léovic puis à F07. Il n’y a plus de deuxième menu pour accepter/refuser, ni d’offre différée après la quête. Ouvrir une question pratique, un menu administratif ou charger un personnage ne verse pas d’argent.

Le versement utilise la fonction réelle du core et l’item `capitale_currency:martin_dor`, 50 unités. Le verrou `CAP_LEOVIC_GIFT` est propre au personnage Nexus. La fonction exige l’inscription validée, la scène 90 et le propriétaire de dialogue PQ02. L’achèvement exige le don reçu et la décision F06 ; le moteur ne peut plus terminer une nouvelle scène non payée.

Les gains antérieurs restent conditionnels : A01-A +1 réputation/+1 impression, T04b-A +1/+1, M04-A 0/+1. F06-C +1/+1 et F06-D −1/−1 restent exclusifs. La réputation totale de PQ02 reste −1 à +3 ; l’impression globale −1 à +4. Aucun +5 automatique n’est réintroduit. Les 165 XP, compétences et les deux tirages historiques du Port sont conservés, un tirage à la fin et l’autre avec la bourse. Le contenu des pools reste intact ; leurs éventuelles pièces sont distinctes des 50 Martins personnels.

## Sauvegardes et compatibilité

Les 111 identifiants de nœud sont identiques à HF7 : 109 nœuds actifs et deux alias conservés uniquement pour les sauvegardes. `f_report` reprend à `f07` ; `post_gift` reprend au menu pratique sans versement. Une quête en cours à F06/F07 reçoit les pièces manquantes à la reprise de la scène, en conservant sa décision sociale. Une bourse déjà marquée reçue n’est pas recréée même si ses pièces ont été dépensées. Fermer/rouvrir, déconnecter ou sauvegarder/charger ne duplique pas le don.

Pour un personnage ayant déjà terminé PQ02 avant HF8 : aucun recalcul de réputation, seconde récompense ou rattrapage automatique à la connexion. L’ancien menu de récupération est supprimé. Ce choix de migration conserve les dossiers historiques et ne relance pas une quête terminée.

## Périmètre exact depuis HF7

| Fichier | Différence |
| --- | --- |
| JAR de quêtes | 4 entrées recompilées/modifiées : métadonnées, graphe, runtime PQ02 et sa classe interne. Les 253 autres entrées sont strictement identiques, dont clients, Gautier, règles, moteur de déplacement, sauvegarde Nexus et mixins. Les instructions de la classe interne de navigation sont identiques à HF7 (`javap -p -c`) ; seules ses références de lignes compilées changent. |
| Core HF1 → HF8 | Seulement `pq02/gift_once_self`, `pq02/complete_once_self` et `pack.mcmeta`. Les 11337 autres entrées sont identiques. |
| Journal R1 → HF8 | Description de la bourse dans la définition PQ02 et métadonnées. Identifiants, objectifs, conditions, plages et autres récompenses conservés. |
| Texte PQ02 | 99 nœuds hors don/retrouvailles inchangés ; 691 positions de lignes de script conservées. Chaque adaptation est recensée. |

PQ01 n’est pas réécrite : Hugues garde l’accroche, cinq questions et son congé ; le seul retrait éditorial reste le choix 6. Le correctif antérieur de Gautier est conservé à l’identique. Les fichiers de MQ01–MQ03 restent identiques à HF7. Le retour MQ03 vers Althéon à 10/20/30, puis les Sentinelles et l’Archivaire à 40/50, la clôture et MQ04 sont exécutés dans les bancs. Les marqueurs de quête et repères A–P ne sont pas déplacés. Aucune recherche ni boucle de versement n’est ajoutée au tick.

## Tests exécutés sur les artefacts livrés

| Suite | Résultat |
| --- | --- |
| pq02_full_system_hf8 | **PASS 1475** |
| merged_native_hf8 | **PASS 2854** |
| hf8_profile_checkpoint_native | **PASS 398** |
| hf8_handoff_native | **PASS 301** |
| client_protocol | **PASS 566** |
| static | **PASS 269** |
| java_rules | **PASS 145** |
| prologue_static | **PASS 388** |
| startup | **PASS 9** |
| duplicate_companion_guard | **PASS 1** |

Le banc complet conserve les JAR de quêtes et Dialogue d’origine de la version testée, avec leurs métadonnées, initialiseurs serveur et mixins, et active EasyNPC + ConfigUI. Le véritable `capitale:core/tick` tourne **773 fois**, avec **245 intercalations entre opérations narratives**. Deux joueurs suivent naturellement les 18 indices A–P. Les échanges au magistrat, l’ascenseur, la fin, les retrouvailles facultatives, le chargement Nexus A–B–A et l’offre suivante d’Aurèle sont contrôlés. Les parcours visitent 90 pages distinctes et testent une fermeture/réouverture native sur 52 pages ; les listes exactes figurent dans `hf8.json`.

La suite de branches exige la destination de chacune des **280 transitions explicites** des 111 enregistrements. Les deux alias ne présentent pas leur ancien dialogue. Les **76 contrôles ciblés du versement** sont exécutés dans le banc complet et dans celui des branches : les quatre réponses, argent avant choix, fermeture, disque Nexus réel, fin sans retour, dix anciennes positions de scène, absence de double paiement, argent déjà dépensé, ancien dossier terminé et refus d’achèvement impayé.

Le banc de sauvegarde vérifie les 111 positions, les sessions, jetons et acteurs pendant les instantanés périodiques Nexus ; la sauvegarde finale conserve son comportement de suspension. Le banc de transitions couvre les départs/reprises acceptés et les interruptions sans progression implicite. Les 108 combinaisons de décision font partie des 145 vérifications Java. L’analyse du prologue couvre 1 291 ressources de PQ01–PQ05 et ne trouve aucune référence littérale interne manquante.

Les contrôles de protocole utilisent l’état et la capture b10 ainsi que les vrais événements Fabric, avec écran et réseau de test. Le démarrage du JAR intact passe ses neuf contrôles, dont le mixin sur la classe OPT3 originale ; Fabric refuse le compagnon PQ02 obsolète en doublon. Les empreintes exactes sont dans `SHA256SUMS.txt` et `verification/hf8.json`.

## Limites des vérifications

Pas de client graphique ni de carte Indivis chargée ; cadrage, obstacles réels, FPS du modpack et voix non testés.

Connexions, réception des chunks, sélection Nexus et confirmation de disponibilité caméra synthétiques. Rédacteur/chargeur/politique/fichiers de profils Nexus OPT3 réels.

Monnaie remplacée par des pépites d’or dans le banc ; CapSkills et tirages aléatoires comptés par fonctions de test. Ces substitutions ne sont pas livrées.

Le mode complet active les initialiseurs de quêtes/Dialogue et le tick core réel avec ses dépendances transitives utiles. Les initialisations comptes/réseau Nexus, RPG et Puffish restent isolées ou déclarées par fixtures ; le modpack complet n’est pas exécuté.

Les 280 transitions sont testées par pages initialisées séparément. Les deux parcours chronologiques utilisent les choix et la marche après initialisation, et ne couvrent pas toutes les combinaisons.

Ces limites concernent le banc ; les substitutions ne sont pas installées dans les JAR/datapacks livrés. La recette graphique sur la carte reste décrite dans `INSTALLATION.md`. Les rapports HF1–HF7 sont historiques et leurs anciennes branches de don ne décrivent plus HF8.

## Installation

Remplacer le JAR de quêtes sur serveur et clients, le core sur serveur et le datapack du journal sur serveur, puis redémarrer complètement. **Aucune commande, pose, liaison ou réinitialisation à refaire.** Le mod reste intégré sous un seul identifiant. Lire `INSTALLATION.md` pour les noms exacts et `COMMANDES_PQ02.md` pour une première préparation éventuelle.
