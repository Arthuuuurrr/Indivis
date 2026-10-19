# Vérifications et recette à terminer

Date : 19 octobre 2026. Base et empreintes : [AUDIT.md](AUDIT.md).

Le tableau et le lancement isolé ci-dessous décrivent les vérifications
initiales de **0.1.0**. Le correctif **0.1.1** ajoute l'audio au JAR et un test
local hors quêtes ; voir [SILENCE_AUDIO_0_1_1.md](SILENCE_AUDIO_0_1_1.md).
Sa compilation propre, ses 17 tests et son contrôle de JAR ont réussi ;
empreintes et limites dans [verification-0.1.1.json](verification-0.1.1.json).
Le test utilisateur avec 0.1.0 confirme les sessions et le parcours textuel,
mais l'absence de banque chargée ne permet pas de valider la lecture vocale.

## Vérifications automatisées

| Vérification | État | Portée de la preuve |
| --- | --- | --- |
| Compilation Java 21, client et serveur, remap Minecraft 1.21.11 | Réussie | Loom 1.17.20, Gradle 9.5.1, Yarn build.6, API 0.141.6 |
| Tests JUnit | 17 réussis, 0 échec | Jetons connexion/session/génération/ligne, doublons, skip, natural end trop tôt, timeout, annulation, personnage, séquence, rôle/PNJ, codecs bornés et catalogue |
| ABI b10 et Nexus PRE34 | Réussie | Lecture des signatures dans les deux JAR exacts fournis, vérification SHA-256 ; pas de faux mod/stub |
| Catalogue ↔ core 1.5.7 | Réussie | 8 tellraw exacts, CAP_QSEQ suivant, timer 20, retrait du timer et dispatch natifs |
| Banque audio | Réussie | OGG Vorbis mono, cible 64 kb/s, durée ffprobe, SHA-256 ; masters FLAC sans pertes |
| Mise à jour incrémentale | Réussie | Relance depuis les 8 masters FLAC : les 8 OGG restent identiques, sans réencodage |
| Wrapper et distribution Gradle | Réussie | SHA-256 officiels vérifiés ; distribution verrouillée dans wrapper.properties |
| Reconstruction propre | Identique octet pour octet | JAR, sources JAR et pack comparés par SHA-256 après `clean build` |
| Démarrage et arrêt Fabric standard | Réussis | Versions réelles ensemble ; pont b10/Nexus résolu, commandes natives présentes, aucun remplacement des JAR |
| Parcours complet et multijoueur en jeu | À faire | Aucune déduction à partir des tests isolés |

Les tests de paquets utilisent les vrais RegistryByteBuf/PacketCodec 1.21.11,
pas un sérialiseur de remplacement. Les tests d'autorité couvrent la logique
pure ; ils n'exécutent pas les récompenses du datapack. La vérification des
sources garantit l'intégration prévue, pas l'absence de tout bug du monde.
Les empreintes et états sont enregistrés dans `verification-results.json` ;
`STARTUP_SMOKE.txt` contient l'extrait de démarrage/arrêt du banc isolé.

### Environnement de compilation local

L'hôte de développement refuse les sockets Unix. Loom échoue dans son simple
test de disponibilité avant de configurer le projet. Un adaptateur **local,
hors dépôt**, désactive uniquement ce test de disponibilité ; aucune permission
réseau n'est ajoutée. Les binaires téléchargés ne sont pas réécrits. Les accès
HTTPS passent par le proxy et le magasin de certificats de l'hôte. Cet adaptateur
n'est ni inclus dans le JAR ni requis par le wrapper/CI sur un hôte standard.

Un essai en environnement Gradle nommé a révélé un mixin existant de Nexus
PRE34 ciblant un nom intermédiaire sans refmap. Cet essai ne constitue pas un
échec en environnement normal : il impose de vérifier les JAR dans un lanceur
Fabric standard, avec le namespace de production, plutôt que de modifier Nexus.

Le test Fabric standard a ensuite atteint `Done` et s'est arrêté proprement
(dernier essai : 12.71 s pour le lancement, les commandes de contrôle et l'arrêt).
Monde neutre de test, écoute uniquement `127.0.0.1:25587`, aucun joueur. Pile : Voices,
EasyNPC Core 7.16, b10, journal b6, Nexus PRE34, RPG RC3, Spell Engine HC RC17,
Spell Power HC RC9, Cloth Config 21.11.153, Player Animation Library
1.1.10+mc.1.21.11, API 0.141.6 et Loader 0.19.5. Les JAR existants sont inchangés.
Les commandes `help hcd_interpellation` et `help easy_npc` ont été résolues.
Ce test **n'inclut pas le monde et les datapacks de quêtes du serveur**, ni une
écoute client ; il ne remplace aucune des recettes ci-dessous. La requête de
clés publiques Yggdrasil a échoué sur cet hôte réseau restreint ; ce serveur
local sans compte s'est néanmoins chargé. Cela ne valide pas l'authentification.

L'essai minimal sans Spell Engine a révélé la dépendance effective non déclarée
de RPG RC3 : `FirearmSpellBridge` accède à `SpellHandlers` dès l'initialisation.
La dépendance a été ajoutée uniquement au banc de test, pas à Voices ni au JAR
RPG. Les builds HC du log ont été réutilisés, pas remplacés par des versions
officielles approximatives. Les mods de contenu absents de cette pile réduite
ne sont pas considérés validés par ce lancement.

## Recette manuelle bloquante avant utilisation sur le serveur

Utiliser une copie de test et les versions auditées. Prendre un état de
progression avant/après chaque essai ; une exécution complète de fonction ou de
récompense doit rester unique. Ne pas faire ces essais sur la production.

| Essai | Résultat attendu | État |
| --- | --- | --- |
| Géraud : 3 prises puis objectif | Texte lisible pendant chaque voix, enchaînement automatique, objectif une fois | Non exécuté en jeu |
| 0.1.1 sans ZIP externe : `/ivvoices`, puis `/ivvoices test` hors dialogue | 8/8 prises, voix locale de Géraud, aucune progression modifiée | À vérifier en jeu |
| Althéon : 3 prises puis choix | Caméra b10 préservée ; menu attendu après la troisième prise ; aucun choix automatique | Non exécuté en jeu |
| Léovic : offre / acceptation | Proposition et escorte inchangées, `QuestChoix` du bon owner, `near_distance` intact | Non exécuté en jeu |
| Suite Aurèle → Roch (#62) | Répliques non doublées encore textuelles ; progression/reprise/récompenses intactes | Non exécuté en jeu |
| Spam clic droit d'ouverture (#115) | Une session native et une génération audio active | Non exécuté en jeu |
| Clic au début/milieu/fin, clic pendant fondu | Invalidation immédiate ; fondu cible 50 ms ; une seule suivante, aucun retour audio obsolète | Logique isolée validée ; acoustique à mesurer |
| ESC, mort, combat, distance, disparition PNJ | Politique b10 respectée, voix arrêtée, aucune progression injectée par le protocole | Non exécuté en jeu |
| Déconnexion / reconnexion / changement monde/personnage Nexus | L'ancienne lecture ne touche jamais le nouveau contexte, même avec même CAP_QSEQ | Autorité isolée validée ; scénario complet à faire |
| Pas de mod / pack absent / hash incorrect / mute / reload pack | Aucun bail vocal, ou restitution du timer texte ; aucun blocage | Vérifications et branches de repli présentes ; scénario complet à faire |
| A parle, B proche | Seul A reçoit le paquet privé, B garde son mixage | Routage serveur privé ; écoute réelle à faire |
| NPC mobile | Source au PNJ courant, pas à l'ancienne position ; aucun ordre de téléportation ajouté | Suivi codé ; déplacement réel à tester |
| Réseau tardif / double fin / mauvais jeton | Paquets obsolètes sans effet, timeout borné, dispatch natif unique | Tests isolés réussis ; réseau dégradé à tester |
| Petite résolution / GUI scale / historique | Pagination b10 automatique répartie sur la durée audio ; historique accessible | À tester visuellement |

Pour les performances (#59, #159), comparer la même scène et les mêmes mods,
résolution et réglages caméra, avec Voices off/on : FPS moyen + bas percentile,
MSPT médian/p95/p99, allocations, mémoire et pauses GC. Relever aussi latence
de début, fin audio → texte suivant et fondu réel. Aucun résultat FPS/MSPT
représentatif du serveur n'a été mesuré ici. Le code ne lit/décode/hache aucun
audio dans les ticks ; il traite seulement les lectures actives et leurs jetons.

## Rencontres ambiantes : phase suivante, non livrée

L'artefact officiel EasyNPC 7.16 contient `MoveToPositionGoal` avec un
constructeur public `(EasyNPC, BlockPos, double, float, int, boolean, Runnable)` :
rayon d'arrivée, timeout, téléportation au timeout explicitement configurable,
callback. Constantes inspectées : PATHFINDING_RANGE=48, timeout par défaut=200
ticks, rayon=1.5. Ce sont des paramètres de cette implémentation, pas une garantie
universelle d'absence de téléportation. Aucun appel à ce constructeur n'a été
ajouté sans la scène/presets réels.

Pour réaliser cette phase et la recette du prologue, il faut :

* Un monde de test ou export de la zone du prologue avec les PNJ et leurs presets
  EasyNPC, les routes/ancres et la configuration dialogue b10 réellement utilisée.
* Pour les deux habitants pilotes : leurs UUID/presets, zone et rendez-vous
  autorisés, comportement initial à restaurer et dialogue de 4–6 répliques validé.
* Pour le casting : WAV/FLAC nommés par les IDs du catalogue et droits de diffusion.
  Le pack synthétique actuel permet les essais techniques en attendant.
* Les sources b10 complètes, si disponibles, pour remplacer proprement l'accès
  aux champs privés par une extension API maintenable ; elles ne sont pas
  prétendues présentes dans PR #104 (sources b7 seulement).

Conception à respecter : événement serveur partagé unique, cooldown de zone
environ 90 s, portée 20 blocs, au plus 2 conversations audibles par joueur,
retour au comportement antérieur sur échec/timeout, pas de markers/armor stands
par défaut. La priorité quête doit atténuer uniquement les voix ambiantes de
Voices du joueur concerné ; 15 %, 0.5 s / 1–2 s restent des valeurs candidates.
Ni cette couche ambiante ni son ducking ne sont annoncés implémentés ici.
