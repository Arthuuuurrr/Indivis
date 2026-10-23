# PQ01 R3-HF3 — caméra du clic droit et diagnostic d’édition

Correctif du 23 octobre 2026, pour Fabric 1.21.11, EasyNPC 7.16.0 et Dialogue b10.

## Installation depuis HF2

1. Arrêter le serveur et remplacer uniquement le JAR journal HF2 par `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF3.jar` dans les mods serveur et clients. Retirer l’ancienne version du même mod.
2. Conserver les deux datapacks R3, Indivis Admin alpha.3, Nexus, Dialogue et EasyNPC. Redémarrer, puis se reconnecter.
3. Ranger la baguette et les autres outils EasyNPC. Faire un clic droit normal de la main principale sur Géraud, sans s’accroupir. Les liaisons réussies sous HF2 restent utilisables ; ne pas recréer les PNJ ni refaire leur liaison pour ce correctif.

Les commandes de pose, liaison et déplacement sont inchangées. [Guide complet](docs/INSTALLATION_ET_POSE_PNJ.md).

Si le personnage est déjà au quai et que l’on veut relire le départ, lancer explicitement `/function capitale:pq01/admin/test_debut`, puis parler au Géraud du navire au large. Terminer cette relecture avec `/function capitale:pq01/admin/test_fin` selon le [guide de tests](docs/TESTS_EN_JEU.md). HF3 ne lance pas cette relecture et ne réinitialise pas le personnage automatiquement.

## Cause dans le code

HF2 a remplacé le déclencheur de fonction par l’action personnalisée native `haute_capitale_quetes:pq01`. Dans le JAR Dialogue b10 fourni, `EasyNpcDialogueBridge.classerInteraction` classe cette action en `CUSTOM`. Le callback de `CaptureOpener` utilise alors `captureExplicite`, dont la valeur de repli est fausse, alors qu’une action `COMMAND` utilise `capture`, qui tient compte de l’activation habituelle du PNJ. Le raccord de capture manquait dans le callback PQ01. En plus, celui-ci revenait silencieusement avant toute caméra lorsque le personnage était en chargement ou dans l’autre phase.

HF3 prépare la capture directement dans `Pq01Bridge.interact`, après les contrôles de liaison, dimension, distance et propriétaire d’acteur personnel, puis avant l’appel de la fonction de quête. Il consulte la configuration de capture du mod Dialogue, respecte son refus de conversation et conserve une session déjà ouverte. Une ouverture échouée empêche l’appel de quête ; le garde de fermeture est libéré dans un `finally`. La caméra de scène individuelle existante reste gérée par le code R3.

Après cette préparation, les conditions de personnage chargé et de phase restent appliquées à la progression. Elles produisent désormais un message sur clic au lieu d’un silence. L’événement `near` ne force pas cette nouvelle ouverture de caméra et reste silencieux lorsque son contexte est inadapté.

## Refus de la baguette malgré le statut OP

Le log serveur fourni et le message client désignent `NetworkMessageRecord.checkAccess`, qui appelle `LivingEntityManager.hasAccess(UUID, ServerPlayer)`. Dans EasyNPC 7.16.0 fourni, cette branche accepte le mode créatif ou le propriétaire du PNJ ; elle ne consulte pas le statut OP. Le PNJ a bien été retrouvé : le journal contient le refus d’accès, et non l’erreur « NPC not found ».

Pour éditer avec la baguette un PNJ dont on n’est pas propriétaire : `/gamemode creative`, effectuer l’édition, puis reprendre son mode habituel, par exemple `/gamemode adventure`. Le clic avec un outil EasyNPC est réservé à l’édition par EasyNPC et Dialogue ; utiliser la main vide pour parler. Les commandes administrateur de liaison/pose PQ01 sont indépendantes de cette baguette.

HF3 ne change pas les propriétaires, les permissions réseau EasyNPC ou ses listes d’autorisations. Aucun nouveau mixin n’est ajouté.

## Vérifications exécutées

- 23 assertions qui exécutent les vrais `Pq01Bridge` et `SequenceBridge` du JAR HF3 : caméra avant l’appel de fonction, joueur non nul, session déjà ouverte, phase incompatible, profil en chargement, personnage absent, `near`, mauvais UUID, portée, monde, dimension de liaison, acteur personnel invalide, refus de Dialogue, capture désactivée et exception d’ouverture. Le contexte de monde, Nexus et les effets caméra sont doublés.
- 3 assertions avec le vrai registre et le vrai `CustomActionDispatcher` EasyNPC : initiateur Minecraft non nul et deux signatures du callback PQ01. Le joueur est alloué sans constructeur ; le retour `getEntity` du PNJ est doublé à `null` pour arrêter avant tout accès au monde. Ce banc vérifie la transmission, pas un paquet de clic ou un serveur complet.
- 80 assertions NBT/actions avec les vraies classes Minecraft 1.21.11 et EasyNPC 7.16.0 ; 109 recherches de membres publics des API réelles ; marche : 17 cas, 26 assertions.
- ASM BasicVerifier : 175 classes, 1 726 méthodes ; CRC du JAR valide.
- Comparaison HF2 → HF3 : métadonnées Fabric, `Pq01Bridge.class` et `Pq01Bridge$1.class` modifiés ; aucune classe ajoutée ou supprimée ; 210 entrées identiques. Tous les mixins, `GuardNbt`, `SequenceBridge`, `Pq01Actions` et les autres composants historiques restent identiques.
- Les deux datapacks R3 ont exactement les mêmes empreintes qu’avant HF3.

**Limites : aucun serveur Fabric complet ni monde utilisateur n’a été démarré.** Les tests couvrent la chaîne de dispatch et l’ordre des appels avec les doublures décrites ; ils ne prouvent pas le rendu caméra, les paquets client, la navigation réelle ou la coexistence de tout le modpack. Le log utilisateur confirme le démarrage de HF2, pas une recette en jeu de HF3.

## Artefact et reproduction

`haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF3.jar` : 466 018 octets.

SHA-256 : `ec608b938930294c9c4c620a1adfb674fd4e0423bde7765d5cfe05b18d7d29da`.

Sources et bancs dans `tools/pq01_full/`. Compiler avec la vraie API Mixin disponible via `INDIVIS_MIXIN_CLASSPATH`, puis lancer :

```bash
python3 tools/pq01_full/build.py
python3 tools/pq01_full/verify_hf3.py --java21 CHEMIN_JAVA21 --minecraft minecraft-intermediary-1.21.11.jar --libraries BIBLIOTHEQUES_DU_BUNDLE_SERVEUR --easynpc EASY_NPC_7_16_0.jar --dialogue DIALOGUE_B10.jar
```

Les classes de doublure sous `tests/interaction_doubles` sont compilées séparément et absentes du JAR livré. Les classes Minecraft, bibliothèques et outils de test ne sont pas redistribués. Les logs utilisateur complets ne sont pas publiés.
