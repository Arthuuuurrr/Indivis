# Haute Capitale — RPG

Noyau RPG du serveur Haute Capitale, Fabric 1.21.11 / Java 21.
Mod ID `haute_capitale_rpg`.

## Ce que c'est, et ce que ce n'est pas

Ce mod **ne remplace aucun mod de classe**. Il se pose à côté d'eux et leur ajoute
ce qu'aucun ne fournit : la notion de classe du joueur.

Il n'enregistre **aucun objet, aucun sort, aucune entité** et **aucun access
widener**. Il ne revendique donc aucun des 1321 identifiants de registre déjà en
service sur le serveur — le retirer ramène le serveur exactement à son état
antérieur.

Deux mixins existent, **côté client uniquement**, pour la caméra RPG : le serveur
dédié n'en charge aucun.

Vérifiable sur le jar livré :

| | |
|---|---|
| identifiants de registre | 0 |
| mixins serveur | 0 |
| mixins client | 2 — dont un seul injecte |
| access wideners | 0 |
| worldgen | 0 |

## Les douze classes

`sorcier` · `sorcier_elementaire` · `sorceleur` · `paladin` · `pretre` ·
`chevalier_de_la_mort` · `berserker` · `voleur` · `forcemaster` · `chasseur` · `barde` · `necromancien`

Douze mods de classe donnent douze classes, mais pas un pour un :
`archers_expansion` enrichit le Chasseur au lieu de former une classe, et
`paladins` en livre deux — Paladin et Prêtre.
Le Nécromancien (`necromancien`, école `spell_power:soul`) vient du mod maison `hc_necromancer`, portage Fabric du pack SamusDev « Awakened Necromancer » (b1, 2026-09-20).

Chaque classe sait quels namespaces et quelles écoles de puissance elle recouvre.
Le noyau observe cette pile, il ne la possède pas.

### Le cas du Chevalier de la mort

Onze classes sur douze enregistrent leurs écoles sous `spell_power:`. Death Knights
utilise `eternal_attributes:blood` et `eternal_attributes:unholy`.

L'alignement se fait **en lecture**, dans `SchoolAlias`, et non par un renommage
des attributs dans le mod d'origine. Ces identifiants sont inscrits dans les
modificateurs de chaque arme et de chaque armure déjà portées : renommer
l'attribut ne renomme pas ce qui dort dans les inventaires, et l'équipement
existant perdrait son bonus sans le moindre message d'erreur.

## Commandes

| Commande | Qui | Effet |
|---|---|---|
| `/rpg classes` | tous | liste les douze classes |
| `/rpg voir` | tous | sa propre classe et son niveau |
| `/rpg voir <joueur>` | MJ | la classe d'un autre |
| `/rpg choisir <classe>` | tous | choisit sa classe, si la config l'autorise |
| `/rpg attribuer <joueur> <classe>` | MJ | impose une classe |
| `/rpg retirer <joueur>` | MJ | retire la classe |
| `/rpg niveau <joueur> <n>` | MJ | fixe le niveau |

## Configuration

`config/haute_capitale_rpg.json`, relu au démarrage.

```json
{
  "choix_libre": true,
  "changement_autorise": false,
  "niveau_max": 50,
  "journaliser_detection": true
}
```

`journaliser_detection` signale au démarrage les classes dont le mod de contenu
est absent. Purement informatif : la classe reste sélectionnable, elle n'a
simplement rien derrière elle.

## Construire et vérifier

```bash
./gradlew build
```

`check` dépend de `runNoyauTest`, un `main()` classique branché sur la tâche —
pas du JUnit. Un `build` ne peut donc pas passer avec un codec de sauvegarde
cassé. Les codecs sont éprouvés en **NbtOps autant qu'en JsonOps** : c'est NBT
qui sert réellement à la sauvegarde du joueur, et un codec qui passe en JSON
peut échouer en NBT sans rien dire.

```bash
./gradlew runNoyauTest    # 25 vérifications
./gradlew runClient       # entre directement dans le monde "coretest"
```

## Toolchain

Loom 1.17.20 · Gradle 9.5.1 (Loom exige ≥ 9.5) · MC 1.21.11 · yarn `1.21.11+build.6`
· loader 0.19.5 · fabric-api `0.141.6+1.21.11`.

Le JDK n'est pas dans le PATH : `~/.gradle/gradle.properties` pointe
`org.gradle.java.home` vers `C:\Users\denne\.jdks\jdk-21.0.12+8`.

## Déploiement

**Ne jamais écraser le jar dans `.minecraft/mods` pendant que le jeu tourne.**
La JVM garde les offsets zip de l'ancien fichier ; le remplacer à chaud fait
exploser le premier chargement paresseux d'une classe pas encore lue, longtemps
après la copie et sur une action sans rapport — alors que le jar sur disque est
parfaitement valide.

## Verrou d'équipement

Deux mécanismes, parce que porter et frapper ne se contrôlent pas de la même façon.

**Armure** — la pièce d'une autre classe est retirée et rendue à l'inventaire, jamais
détruite. Les emplacements d'armure sont distincts de l'inventaire principal, donc la
pièce rendue n'y remonte pas toute seule.

**Arme** — l'attaque est refusée, l'arme reste en main. Arracher une arme ne marche pas :
la barre d'action *fait partie* de l'inventaire, si bien qu'on la rend dans la case même
qu'on vient de vider et qu'elle revient aussitôt en main. Refuser le coup atteint le but
sans se battre contre l'inventaire du joueur.

Un objet appartient à la classe qui possède son **namespace**. Un objet de `paladins:` est
accepté par le Paladin *et* le Prêtre ; le Chasseur accepte `archers:` *et*
`archers_expansion:`. Arsenal, Armory, Relics et Jewelry ne sont revendiqués par personne :
leurs équipements restent libres pour toutes les classes.

**Désactivé par défaut** — installer le noyau sur un serveur en cours ne change rien tant
que l'administrateur ne l'a pas décidé.

**Limite assumée** : le lancement de sorts passe par Spell Engine et n'est pas intercepté.
Un bâton d'une autre classe ne peut pas frapper, mais il peut encore lancer ses sorts.

## Verrou de sorts

Ferme la porte que le verrou d'équipement laissait ouverte : une arme d'une autre classe
ne pouvait déjà plus frapper, mais elle pouvait encore lancer ses sorts.

Le refus passe par `SpellEvents.CASTING_ATTEMPT.PRE`, le point d'extension que Spell Engine
expose exactement pour ça — sa propre documentation le décrit comme « la porte devant chaque
lancement, où les mods peuvent injecter un verdict ». Aucun mixin, aucun contournement.

Deux vérifications, pas une : l'**objet** qui porte le sort, et le **sort lui-même** — un sort
`wizards:` reste un sort de Sorcier, même lié à une arme libre d'Arsenal.

**Spell Engine reste facultatif.** Il est déclaré en `modCompileOnly` et la classe qui le
référence n'est chargée que s'il est présent ; sans lui le noyau démarre normalement et
journalise que le verrou de sorts est inactif.

## Progression

Deux sources d'expérience, réglables et désactivables séparément :

| Source | Défaut | Événement |
|---|---|---|
| Tuer une créature | 12 points | `ServerLivingEntityEvents.AFTER_DEATH` |
| Lancer un sort **de sa classe** | 4 points | `SpellEvents.SPELL_CAST`, action `RELEASE` |

Un joueur tué ne rapporte rien — sinon la progression se farme entre joueurs. Un sort qui
n'appartient à aucune classe ne rapporte rien non plus : la progression récompense la pratique
de sa classe, pas l'usage d'un objet générique. Seul le lancement effectif compte, jamais
l'incantation ni les ticks d'un sort canalisé, sinon un sort maintenu paierait plusieurs fois.

**Courbe** — coût du passage de N à N+1 : `base × N^exposant`, par défaut 75 et 1,1. Soit
**129 265 points pour atteindre le niveau 50**, environ 10 800 créatures. Ce sont les mêmes
paramètres que les métiers de Haute Capitale, pour que les deux progressions du serveur aient
le même rythme.

Ce total est vérifié par le harnais, et affiché à chaque exécution. C'est délibéré : une courbe
qui paraît douce peut demander des dizaines de milliers d'actions — une base 100 avec un exposant
1,8 réclamait presque deux millions de points.

L'expérience stockée est celle **du niveau courant**, pas le cumul depuis le niveau 1 : l'affichage
« 120 / 340 » se lit directement, et changer la courbe n'invalide pas les sauvegardes.

## Caméra RPG

Caméra troisième personne par-dessus l'épaule, **entièrement côté client**. Aucun paquet,
aucune logique serveur : distance, offsets et préférence d'épaule sont des réglages
d'affichage.

### Une seule touche : F5

F5 parcourt, dans cet ordre :

```
1re personne vanilla → 3e personne vanilla → vue de face vanilla → RPG « epaule » → RPG « mmo » → 1re personne
```

Les trois vues vanilla restent **strictement** vanilla — mesuré en jeu : `4.00 / 0.00 / 0.00`,
la caméra RPG n'y touche pas. Les styles RPG sont des positions du cycle à part entière, et
en ajouter un dans la config ajoute une position.

**Comment F5 est interceptée sans mixin.** Le jeu lit ses touches dans
`MinecraftClient#handleInputEvents`, appelé depuis `tick()`. L'évènement `START_CLIENT_TICK`
de Fabric API se déclenche en tête de ce même `tick()` : en y consommant les appuis de F5 par
`wasPressed()`, qui décrémente le compteur, le jeu n'en voit plus aucun. Les deux effets de
bord du F5 vanilla — `onCameraEntitySet` en entrant ou sortant de la première personne,
`scheduleTerrainUpdate` — sont reproduits à l'identique (vérifiés dans le bytecode 1.21.11).

Pendant une cinématique ou un dialogue, F5 est avalée : quelqu'un d'autre tient la caméra.

`f5_cycle: false` rend F5 au jeu, et la caméra RPG s'applique alors dès que la vue est en
troisième personne.

### La règle qui gouverne tout le module

**La caméra est déplacée, le joueur ne tourne jamais.** Ni yaw, ni pitch, ni paquet de
rotation.

C'est ce qui rend le module invisible au reste de la pile : Better Combat construit son cône
d'attaque sur la position et la rotation du joueur (`TargetFinder`), Spell Engine vise depuis
`caster.getEyePos()` et `getRotationVec()`, et le jeu lui-même tire son rayon de visée depuis
le joueur — `GameRenderer.pick()` fait `player.raycastHitResult(...)`, pas un rayon depuis la
caméra. Déplacer la caméra ne change donc **rien** à la portée, au minage, au ciblage
d'entité, aux flèches ni aux sorts.

Reste le décalage d'affichage : le centre de l'écran ne désigne plus la direction de tir. Il
est corrigé en dessinant un **réticule projeté** à l'endroit réellement visé — un calcul
d'écran, pas une correction de visée.

### Cinématiques

`CameraOverrideManager.enterCinematicMode(raison)` / `exitCinematicMode(raison)` : pendant une
suspension, le mixin ressort à sa première instruction — pas d'offset, pas de zoom, pas de
collision, pas de bascule de perspective. L'ordre d'application face à un autre mod de caméra
devient donc sans objet.

Le compteur n'est pas un booléen : une cinématique de boss enchaîne plusieurs handlers sur la
même cible, et un booléen produirait un clignotement à chaque transition.

Une sonde lit en plus `BossesRiseClientCinematicCamera.isCameraActive()` par `MethodHandle` —
Bosses'Rise n'expose aucune API, ses cinématiques partent de keyframes GeckoLib. Pas de
dépendance de compilation sur son JAR : une évolution du mod dégrade la sonde au lieu de
casser celui-ci.

**Garde-fou obligatoire** : Bosses'Rise documente lui-même qu'un boss qui cesse d'être dessiné
cesse d'être animé, et qu'une cinématique qui cesse d'être animée n'atteint jamais son keyframe
de fin. Sans le watchdog, la caméra RPG resterait suspendue jusqu'à la déconnexion.

### Ce que le module ne touche pas

| | pourquoi |
|---|---|
| rotation du joueur | Better Combat, Spell Engine et l'autorité serveur en dépendent |
| `GameRenderer#getFov` | déjà partagé entre Bosses'Rise et Zoomify |
| `setPerspective` | Bosses'Rise s'en est réservé la sauvegarde/restauration |
| la molette | appartient à Zoomify (`scrollZoom: true`) |

### Touches

`F5` vue suivante · `O` épaule · `,` éloigner · `.` rapprocher · `F9` overlay d'état ·
bascule ON/OFF non liée.

Le zoom agit sur le style RPG courant et y reste : chaque style garde sa propre distance.

Choisies parce qu'elles sont réellement libres dans l'`options.txt` du serveur : `C` y est
déjà liée quatre fois, `V` et `H` trois fois, `]` appartient à Xaero.

### Réglages

`config/haute_capitale_rpg_camera.json`, relu au démarrage et réécrit à chaque changement en
jeu. `actif: false` rend la caméra strictement vanilla.

```json
"styles": [
  { "nom": "epaule", "distance": 5.5, "offset_horizontal": 0.55, "offset_vertical": 0.25 },
  { "nom": "mmo",    "distance": 7.0, "offset_horizontal": 0.0,  "offset_vertical": 0.9  }
]
```

Une liste vide ou absente ramène ces deux styles ; un style sans nom en reçoit un.

`passage_premiere_personne` est **désactivé par défaut, et ce n'est pas un oubli** : c'est la
seule option qui écrit dans `setPerspective`, la variable que Bosses'Rise s'est réservée.

### Mixins

Deux classes, un seul point d'injection.

| | |
|---|---|
| `CameraMixin` | un `@Inject` au **TAIL** de `Camera#update` — donc avant que `GameRenderer` ne construise le frustum, ce qui laisse Sodium et Distant Horizons cohérents |
| `GameRendererAccessor` | un `@Invoker` sur `getFov`, sans une ligne ajoutée dans le flux |

Fabric API n'expose aucun évènement de caméra : cette injection est inévitable. Elle est aussi
suffisante — tout le reste passe par `ClientTickEvents`, `HudElementRegistry`,
`KeyBindingHelper` et `ClientPlayConnectionEvents`. `moveBy` et `setPos` sont `protected` en
1.21.11 : **aucun access widener**.

### Vérifier

```bash
./gradlew runCameraTest                 # harnais hors jeu
./gradlew runClient -PcameraSelfTest    # autotest en jeu, le client s'arrête seul
```

L'autotest entre dans le monde, met le joueur en l'air pour dégager le champ, puis mesure la
position réelle de la caméra dans son propre repère à chaque étape : cadrage RPG, suspension
de cinématique, restauration, module coupé, épaule gauche, zoom. Le verdict est dans
`run/logs/latest.log`.

## État

**Étape 1 — noyau.** Classes, données joueur, configuration, commandes, verrou d'équipement,
verrou de sorts, progression.

Testé avec les trente mods de la pile RPG réelle : démarrage propre, onze classes détectées,
`/rpg classes` rend 11, aucune erreur imputable au noyau.

Chemin joueur validé en client de dev sur deux sessions : choix de classe, niveau, bornage,
puis après redémarrage — classe et niveau conservés, changement refusé par la config, retrait
effectif. **7/7.**

Verrou d'armure validé avec les vrais objets de Berserker et de Paladin. **5/5.**

Verrou de sorts : crochet enregistré au runtime contre le vrai Spell Engine, et démarrage
sans Spell Engine vérifié. **Le refus d'un lancement n'a pas pu être automatisé** — Spell
Engine n'expose pas de commande de lancement, il faut appuyer sur la touche en jeu.

Progression validée en jeu : seuil de niveau, montée au seuil exact, franchissement de
plusieurs paliers d'un coup, bornage au maximum, et gain effectif sur la mort d'une créature.

**Les onze classes** sont couvertes par la matrice du harnais : 132 combinaisons
classe/namespace vérifiées — chaque classe possède ses propres namespaces et refuse
ceux des autres, et Paladin/Prêtre est bien le seul couple à en partager un.

Le verrou lui-même n'a été observé **en jeu** que sur Berserker et Paladin. Le client de
développement ne tient pas la charge des trente mods de la pile réelle : trois tentatives,
trois blocages ou sorties en erreur. Le mécanisme est donc prouvé en jeu sur un couple de
classes, et la donnée des onze est prouvée par le harnais — mais pas les onze en jeu.

Harnais `NoyauTest` : **41 vérifications**, branché sur `check`.

Suite : arbre de compétences déjà couvert par un autre mod ; reste à confirmer en jeu le
refus d'un lancement de sort et le verrou sur les neuf classes non observées.

---

**Étape 2 — caméra RPG.** Module client complet : machine à états, lissage, collision,
suspension pendant les cinématiques, réticule projeté, overlay de debug, mode OFF.

Harnais `CameraTest` : **52 vérifications** hors jeu — comptage des suspensions, indépendance
du lissage vis-à-vis des images par seconde, signes de la projection, bornage de la config,
prise de caméra par un dialogue, ordre du cycle F5.

Autotest en jeu (`-PcameraSelfTest`) : **19 mesures, 0 échec**, reproductibles. La position
réelle de la caméra est mesurée dans son propre repère à chaque étape. Le cycle F5 y est
parcouru position par position, et un appui est injecté dans le **vrai compteur de la
touche** (`KeyBinding.onKeyPressed`) pour prouver que l'interception passe avant le jeu :
une seule position avancée, jamais deux.

| étape | mesuré (distance / latéral / vertical) |
|---|---|
| caméra RPG, épaule droite | 5.50 / +0.55 / +0.25 |
| pendant une cinématique | 4.00 / +0.00 / −0.00 — **vanilla exact** |
| après la cinématique | 5.50 / +0.55 / +0.25 — **restauré à l'identique** |
| module coupé (OFF) | 4.00 / +0.00 / −0.00 |
| épaule gauche | 5.50 / −0.55 / +0.25 |
| zoom à 3 blocs | 3.00 / −0.55 / +0.25 |
| espace étroit (murs à 3 blocs) | 2.90 — rentrée, et hors de la pierre |
| sortie d'espace étroit | 5.50 / +0.55 / +0.25 |
| F5 → RPG « mmo » | 7.00 / +0.00 / +0.90 |
| F5 → 1re personne → 3e personne vanilla | 4.00 / +0.00 / −0.00 — **intacte** |
| F5 → vue de face → RPG « epaule » | 5.50 / +0.55 / +0.25 |
| F5 pendant une cinématique | ignorée, perspective inchangée |
| vraie touche F5 (compteur réel) | une seule position avancée, epaule → mmo |

La sonde s'est branchée sur le vrai Bosses'Rise au démarrage
(`isCameraActive()` résolue), avec quinze mods chargés dont GeckoLib.

**Deux bugs trouvés par l'autotest, invisibles autrement :**

1. La marge de collision était retranchée **même en terrain dégagé** — la caméra se tenait en
   permanence 0,20 bloc trop près, sans rien pour le signaler.
2. Le lissage portait sur l'**altitude** de l'ancre et non sur la hauteur des yeux : un filtre
   exponentiel retarde toujours une entrée à vitesse constante, et un joueur en chute libre
   traînait la caméra 1,4 bloc au-dessus de lui.

**Reste à confirmer par un humain** : la sensation en combat avec Better Combat, la visée des
sorts en jeu, et une vraie cinématique de boss déclenchée par ses keyframes — l'autotest ouvre
la suspension par l'API, pas par un boss.
