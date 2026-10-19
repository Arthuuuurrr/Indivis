# Indivis Voices — prototype prologue 0.2.1

Référence : [issue #167](https://github.com/Arthuuuurrr/Indivis/issues/167).
Mod Fabric **indépendant**, côté serveur et client, pour Minecraft 1.21.11,
Loader 0.19.5, Fabric API 0.141.6+1.21.11, Java 21. Adaptateur ciblé sur
`haute_capitale_dialogue 0.1.0+1.21.11.b10`, EasyNPC 7.16.0, Nexus PRE34 et le
**core 1.5.7 fourni**, identifié par hash dans [l'audit](docs/AUDIT.md).

Le JAR couvre **20 répliques : 18 prises humaines d'Arthur pour Géraud et Althéon,
et 2 démonstrations synthétiques pour Léovic**. Le réveil, le rappel, les trois
invitations du capitaine, son introduction et les huit réponses aux questions
avant l'accostage disposent des prises humaines. Les autres répliques restent
textuelles. [La table de correspondance](docs/INTEGRATION_HUMAINE_LOT1.md) donne
chaque code du PDF, son fichier, son texte et sa séquence native. Pour
`P01-GER-005`, la première prise est utilisée ; les trois variantes restent
dans l'archive de préparation.

**Les 20 OGG et leur index sonore sont inclus dans le JAR et chargés
automatiquement. Aucun pack séparé n'est nécessaire.** Les OGG sont conservés
dans `demo-resourcepack/` pour partager les mêmes fichiers avec l'export
facultatif. Les masters humains restent dans l'archive sans pertes déjà fournie,
hors du mod ; `demo-masters/` conserve uniquement les masters synthétiques.
Ce choix remplace, à la demande d'Arthur le 19 octobre, la distribution séparée
initialement prévue dans #167 ; les masters restent hors du mod.

## Essai sur une installation de test

1. Garder la pile actuelle : EasyNPC Core/Config UI 7.16, dialogue b10, RPG RC3,
   journal b6, Nexus PRE34, core 1.5.7 et hc_quetes b21. Ne remplacer aucun de ces
   fichiers par les anciennes versions de main.
2. Remplacer l'ancien JAR Voices par `build/libs/indivis-voices-0.2.1+1.21.11.jar`
   dans les **mods du serveur de test et du client**, puis redémarrer les deux.
   Ne garder qu'un seul JAR Voices. Aucun pack à télécharger ou activer ; les
   sons font partie des ressources du mod. Aucun datapack d'intégration en plus.
3. Vérifier `/ivvoices` : **20/20 prises vérifiées**, voix actives, pont b10 prêt,
   canal serveur présent et volumes Minecraft non nuls. Hors dialogue,
   `/ivvoices test P01-ALT-010` joue la première réponse sur la découverte.
   Chaque code de la table peut être testé ainsi ; le texte attendu est affiché.
   `/ivvoices test` joue la première réplique de Géraud localement, sans PNJ,
   caméra, paquet de progression ni action de quête ; pas besoin d'un personnage neuf.
   Une prise manquante ou modifiée reste textuelle. Un joueur sans Voices garde
   le dialogue natif ; un client Voices sur un serveur sans Voices aussi.
4. Jouer le prologue par ses déclenchements habituels avec un personnage de test.
   Les répliques couvertes s'enchaînent à la fin de l'audio ; les choix restent
   ceux de b10 et du datapack. Clic gauche/droit, espace ou entrée pour passer.
   ESC, historique, touches de choix et caméras restent gérés par b10.

Commandes client, sans privilège serveur : `/ivvoices test`, `/ivvoices on`, `/ivvoices off`,
`/ivvoices volume 70`, `/ivvoices subtitles on|off`. Les sous-titres supplémentaires
de Voices sont facultatifs et désactivés par défaut. Le texte et les choix de
b10 assurent l'affichage normal pendant la voix. Options persistées dans
`config/indivis-voices.json`.
Le volume Voices agit par source, sans modifier les volumes des autres sons.
Les réglages master/catégorie voix de Minecraft restent applicables.

## Compilation et vérifications

JDK 21 + Python 3 ; wrapper Gradle 9.5.1 avec SHA-256 de la distribution,
Loom 1.17.20, Yarn 1.21.11+build.6. Aucun JAR privé n'est nécessaire au build.

```sh
cd sources/indivis-voices
./gradlew --no-daemon clean build
python3 tools/verify_inputs.py --core /chemin/capitale_core_1.5.7.zip
python3 tools/verify_abi.py --dialogue /chemin/dialogue-b10.jar --nexus /chemin/nexus-pre34.jar
```

`build` compile et remappe le JAR client/serveur, exécute les tests d'autorité et
les tests réels des codecs, vérifie la banque et son inclusion dans le **JAR
remappé livré** (20 OGG, SHA-256, codes de production et index sonore), puis produit aussi le ZIP
audio facultatif. Les masters WAV/FLAC ne sont pas embarqués.
La vérification `--core` exige exactement le ZIP audité puis contrôle chaque
texte et sa séquence, le retrait natif du timer et le dispatch. `verify_abi`
lit les signatures des classes **dans les JAR réels**, sans initialiser les mods.
Les archives ont un ordre et des dates normalisés. Les résultats et les limites
des essais sont dans [VALIDATION.md](docs/VALIDATION.md).

`./gradlew runServer --args nogui` utilise exclusivement `build/smoke-server/`.
Ce lancement de développement n'embarque pas les mods privés ni le monde du
serveur. Il ne valide donc pas à lui seul la compatibilité du prologue.

## Synchronisation et progression

**Correctif 0.2.1 :** le dispatcher du core 1.5.7 fourni permettait à plusieurs
branches de s'exécuter dans le même appel : après la première voix de Géraud,
les deux textes suivants et l'objectif étaient traversés avant le prochain tick.
Voices corrige la compilation de cette seule fonction, identifiée par son ID
et une empreinte stricte, pour retourner après la première branche exécutée.
Le datapack sur disque, les textes, les ID, les choix et les récompenses restent
inchangés. Aucune correction n'est appliquée à une autre version de la fonction.
Voir [le diagnostic et la recette](docs/ENCHAINEMENT_0_2_1.md).

Le serveur reconnaît un `tellraw` exact envoyé au joueur via
`ServerPlayerEntity.sendMessageToClient(Text, boolean)` — appel vérifié dans
`TellRawCommand` 1.21.11. Il attend la fin du tick pour lire le CAP_QSEQ posé
**après** le texte par la fonction. Il faut une session b10 réelle, le personnage
Nexus actif, le monde attendu et un client disposant de la prise vérifiée.

Pour les répliques séquencées, il renouvelle CAP_QSEQ_TIMER à 2 avant le tick du
core. Ce dernier continue de retirer 1 et reste l'unique propriétaire du dispatch.
Une fin naturelle admissible ou un clic met le timer à 0 ; le core enchaîne.
Une ressource absente ou un timeout rétablit le délai texte capturé. Un changement
de séquence/personnage/monde annule le jeton, sans écrire dans la nouvelle
progression. Aucune récompense ni fonction de quête n'est appelée par Voices.
Les trois invitations et le rappel de Géraud sont autonomes : CAP_QSEQ doit
rester à 0 et Voices n'écrit jamais leur timer, même en fin de voix ou sur échec.
Un choix natif qui change la séquence annule leur lecture. Le client attend au
plus une seconde l'écran b10 et la capture du texte exact avant de démarrer ;
il revient au texte si cette association n'est pas disponible.

Protocole v1 : capacités bornées (32 prises pour ce MVP), epoch de connexion,
session b10, génération et ID stable de réplique. Réponses périmées/dupliquées
ignorées ; contrôle de la durée minimale d'une fin naturelle ; timeout serveur
à durée catalogue + 5 s. Un clic volontaire peut écourter la voix, jamais choisir
une réponse. Chaque paquet de lecture est envoyé au seul joueur concerné.

Le client suit l'UUID **et** l'entity ID du PNJ à sa position actuelle. Un skip
invalide immédiatement l'ancienne lecture et son retour réseau. Une enveloppe
de 50 ms atténue seulement cette source, mise à jour par tick et rendu via la
file audio native ; la suivante attend l'arrêt. Une chute prolongée de FPS peut
retarder l'application du fondu : la mesure acoustique en jeu reste nécessaire.
Le suivi de l'entité ne crée aucun objectif de mouvement EasyNPC.

L'adaptateur b10 concentre les accès réfléchis à ses signatures vérifiées. Il
cadre la réplique déjà capturée et conserve l'historique et les choix ; il maintient
la fenêtre de capture pendant la voix pour empêcher une fermeture par silence.
Sur échec de lecture, il restaure les paragraphes natifs si aucune nouvelle
capture ne les a remplacés. Une autre version de b10 ou une interface manquante
désactive le pont et conserve le texte. Les sources b10 complètes permettront
plus tard de remplacer ces accès privés par une petite API explicite.

## Ajouter ou remplacer une prise

`src/main/resources/indivis-voices/catalog.json` lie chaque ID stable au locuteur,
texte exact, fonction d'origine, CAP_QSEQ suivant, langue, asset, durée réelle,
takeVersion et SHA-256. Le manifeste du pack reprend ce catalogue. Ne pas changer
un ID quand une interprétation est remplacée. Les textes ne sont pas normalisés
approximativement : une réécriture du lore nécessite une nouvelle validation.

Fournir un WAV ou FLAC par ID, avec autorisation de diffusion de l'interprète.
La [table du lot humain](docs/INTEGRATION_HUMAINE_LOT1.md) fait foi pour les
18 codes reçus ; le [script initial](docs/RECORDING_SCRIPT.md) conserve les
huit textes du prototype et les deux textes de Léovic restant à enregistrer.

```sh
python3 tools/build_voice_bank.py --masters /chemin/prises \
  --revision prologue-fr-2 --take-version prises-humaines-v1
./gradlew build
```

Le script encode les masters sans pertes en OGG Vorbis mono cible 64 kb/s, mesure
les durées avec ffprobe et renouvelle les hashes. Une prise dont le master, la
version et l'OGG sont inchangés est conservée sans réencodage. Le texte exact
et les IDs restent en place. Distribuer le nouveau JAR/catalogue avec les voix
incluses aux clients et au serveur : une ancienne révision ne déclenche aucune
attente serveur. Un resource pack externe peut toujours surcharger les ressources
du mod ; si ses fichiers ne correspondent plus aux hashes, la prise reste textuelle.
Les fichiers audio peuvent partager le même asset quand l'enregistrement est
réellement identique. Ne jamais réencoder un OGG en guise de master.
Pour une prise humaine remplacée, mettre aussi à jour sa provenance dans
`human-takes.json` : les contrôles refusent un échange d'audio non documenté.
Les six anciens masters synthétiques remplacés sont archivés dans
`demo-masters/original-synthetic/`, hors du chemin de reconstruction courant.

## Périmètre restant et retour arrière

Les rencontres ambiantes partagées, leur ducking et les déplacements de deux PNJ
ne sont pas livrés dans ce MVP. [Le plan de validation](docs/VALIDATION.md) précise
les données nécessaires et les essais à mener, sans inventer positions ni routes.
Les comparatifs FPS/MSPT et le parcours Géraud → Roch à deux joueurs restent
des critères de recette, pas des résultats déduits d'une compilation.

Retour arrière : retirer seulement Voices sur l'environnement de
test. Aucun fichier de quête, ID, permission EasyNPC ou progression sauvegardée
propre à Voices n'a été ajouté. Le dernier bail natif expire en au plus deux ticks
de core si le mod disparaît ; un arrêt normal restaure les délais texte.
Pas de migration, pas de remplacement du core sur disque et pas de déploiement
automatique. Retirer Voices retire aussi sa garde de compilation ; le défaut
d'enchaînement du core original réapparaît alors jusqu'à sa correction native.
