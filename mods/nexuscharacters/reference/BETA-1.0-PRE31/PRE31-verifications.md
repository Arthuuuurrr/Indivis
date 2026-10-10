# NexusCharacters Indivis PRE31 — changements et vérifications

10 octobre 2026. Minecraft Java 1.21.11 / Fabric.
Version : `1.0.0-beta.31+indivis.1.21.11`.

## Résultat

Cette version réduit le travail redondant lors des changements de pose en première personne et les allocations de conversion des surfaces. Les 320 états géométriques comparés et les 23 captures des menus correspondent à PRE30. Le recalcul animé de troisième personne reste coûteux ; cette passe ne démontre pas de gain CPU significatif sur ce chemin ni de hausse globale des FPS.

## Installation

Remplacer le JAR NexusCharacters précédent par `NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE31.jar`. Conserver un seul JAR NexusCharacters dans `mods`. Garder les dépendances habituelles. Le ZIP contient le même JAR et cette notice : extraire le JAR avant installation.

Les changements portent sur le rendu client. Le format des personnages et le protocole sont inchangés ; cette passe n'introduit pas de migration des sauvegardes.

PRE30 utilisée, SHA-256 :
`1c164be6dc8eef9372120ae384b65c5d2be8ebaa7fed67461b986726a5cff922`

PRE31 livrée, SHA-256 :
`d0d725337353dcc7328b86a46ace83ca82b66b7680416a8b555900de764fe203`

## Modifications

### Conversion en tableaux primitifs

`SurfaceGeometry.write` appelle un convertisseur qui écrit directement dans un tableau `float[]`. Il remplace l'ancienne construction d'une liste `Float` suivie d'une conversion. La validité des faces est calculée une fois, puis le tableau est dimensionné selon le nombre d'éléments du format natif. Les normales, les coordonnées, les UV, l'ordre des sommets et la triangulation avec sommet doublé restent identiques.

Le convertisseur ne conserve aucune donnée entre deux appels. Il n'ajoute pas de cache global de faces ou d'entités.

### Première personne

Dans ce chemin, les meshes utilisent des faces locales : une modification des matrices de pose du corps ne change pas nécessairement ces faces. PRE30 sérialisait de nouveau les faces des six parties à chaque changement global de pose.

PRE31 conserve les tableaux déjà préparés lorsque le modèle était déjà en première personne et que les templates et les transformations relatives des couches supérieures n'ont pas changé. Les changements de template ou de transformation relative déclenchent toujours l'actualisation. Le passage depuis la troisième personne impose également la réécriture dans la représentation locale. Après suppression de l'état ou rechargement, la préparation initiale demeure nécessaire.

Les six parties restent prises en charge. Les matrices et snapshots continuent d'être actualisés, les meshes sont toujours réinstallés à la fin de chaque passage et les compteurs existants restent présents. Aucun plafonnement de fréquence, aucune suppression d'animation ni réduction des couches 3D.

## Mesures

Client Minecraft réel, sur son thread de rendu : Java Temurin 21.0.12.1, Fabric Loader 0.19.5, Fabric API 0.141.6, 3D Skin Layers 1.10.2, Sodium 0.8.7 et ImmediatelyFast 1.14.2. Écran virtuel 1920 × 1080 et rendu Mesa llvmpipe. Les mesures CPU viennent de `ThreadMXBean`, les allocations de `com.sun.management.ThreadMXBean`. Les diagnostics Nexus sont désactivés pour ces comparaisons.

### Poses animées

Cinq échantillons par version, même banc et mêmes assets. Première personne : 1 000 appels animés de chauffe puis 2 000 par échantillon ; les parties inférieures changent de pose, les faces locales restent stables. Troisième personne : préparation stable de chauffe puis 100 poses animées par échantillon, avec rotations et échelles variables.

| Traitement | PRE30 CPU/appel | PRE31 CPU/appel | PRE30 octets/appel | PRE31 octets/appel |
|---|---:|---:|---:|---:|
| Poses animées en première personne | 368,805 µs | 2,515 µs | 2 143 280 | 0 |
| Poses animées en troisième personne | 71,985 ms | 71,906 ms | 105 348 118 | 101 370 812 |

La première ligne établit une baisse CPU d'environ 99,3 % sur ce traitement précis et supprime les allocations mesurées après préparation. La seconde établit une baisse d'allocations d'environ 3,8 %, mais son écart CPU ne suffit pas à conclure à un gain. Les changements de pose continuent d'être traités à chaque invocation : 2 000 changements par échantillon en première personne et 100 en troisième personne.

Ces valeurs ne mesurent pas l'ensemble du rendu d'une frame, les objets tenus, tous les équipements ou tous les mods. Elles ne doivent pas être traduites en une hausse équivalente des FPS.

### Chemins immobiles après chauffe prolongée

Un contrôle supplémentaire utilise 100 000 appels de chauffe, puis neuf échantillons de 20 000 appels, car le premier banc montrait encore une évolution du temps des passages très courts pendant la compilation JIT.

| Traitement | PRE30 CPU/appel | PRE31 CPU/appel | Octets/appel dans les deux versions |
|---|---:|---:|---:|
| Pose immobile, passage Posed | 1,041 µs | 1,065 µs | 0 |
| Aperçu complet de deux modèles | 25,615 µs | 27,041 µs | 18 992 |

Ces chemins ne bénéficient pas d'une nouvelle optimisation dans PRE31. Aucun gain n'y est annoncé. Les écarts observés et les données brutes sont conservés ; il n'est pas affirmé que tous les temps d'exécution sont identiques.

### Convertisseur isolé

Sur une liste de 500 faces du banc isolé, cinq échantillons de 1 000 écritures après 500 appels de chauffe : CPU médian 66,84 → 12,21 µs, allocations 383 992 → 42 240 octets par écriture. Ce test mesure uniquement la conversion, avec le même surcoût de réflexion de test pour les deux versions.

## Vérifications effectuées avant livraison

- Convertisseur réel de PRE30 et de PRE31 chargé séparément : 4 000 cas et 2 962 626 assertions. Même taille et mêmes bits bruts de chaque valeur ; faces vides et invalides, triangles, quadrilatères, polygones triangulés, UV, zéros signés et valeurs non finies inclus dans les cas appropriés.
- Géométrie Minecraft : 320 états comparés, coordonnées, UV et normales identiques après classement canonique des polygones. Les tableaux peuvent changer d'ordre selon les collections existantes ; la comparaison neutralise cet ordre sans modifier les valeurs.
- Le banc de PRE31 effectue 2 337 assertions de rendu : modèles classique/slim, poses et échelles variables, tête masquée, changement de template, changement de perspective, suppression/reconstruction de l'état, 120 poses supplémentaires en première personne, invalidation par transformations relatives. Il vérifie aussi que les douze tableaux sont réutilisés lorsque seules les poses inférieures changent.
- Menus : 23 scènes et 688 assertions, cinq races, échelles GUI 2/3/4, création/sélection, sliders, rotation et noms colorés. Les 23 captures sont identiques pixel par pixel à PRE30.
- Monde isolé : 52 assertions et 346 frames, première personne avec animation de la main, troisième personne, inventaire, rechargement réel des ressources, puis retour en première personne. Présence des injections natives vérifiée sur les six parties et mode première personne vérifié avant et après rechargement. La modification client du personnage dans ce banc peut être remplacée par le profil serveur : ce résultat ne prétend pas prouver un changement de personnage sauvegardé en multijoueur.
- Nouveaux fichiers : `hair_930.png`, `beard_930.png` et `outfit_930.png`, ajoutés dans le seul mod de test, sont découverts et rendus avec le corps 2 et les modèles classique/slim. 19 assertions ; surfaces natives présentes et capture inspectée. Ce sont des copies d'assets existants sous de nouveaux identifiants : le test vérifie la découverte et le rendu, pas la validité artistique de n'importe quel futur PNG.
- Inspection visuelle de l'aperçu des nouveaux assets, de la vue arrière du menu, de l'inventaire et de la première personne après rechargement.

## Audit du périmètre

Seules `SurfaceGeometry.class`, `PosedSurfaceSupport.class` et la métadonnée de version/description changent. `PrimitiveSurfaceWriter.class` est ajouté. Aucun fichier n'est supprimé.

**5 209 entrées existantes, dont les 4 966 assets, sont identiques octet par octet à PRE30.** Les mixins, les menus, les palettes, la composition des skins, le catalogue, le serveur, le protocole et la persistance restent identiques.

L'audit d'instructions vérifie 27 méthodes et démontre que le corps de `applyPRE30` redevient exactement celui de PRE30 après retrait du contrôle ajouté de six instructions. La découpe, les epsilon, les priorités et les transformations géométriques restent donc ceux de PRE30. Les états faibles, le cache local existant et les espaces de matrices de PRE30 sont conservés.

Les journaux du banc comportent les erreurs attendues d'authentification/Realms en mode hors ligne et une traduction Realms non téléchargée ; ils ne comportent pas d'échec des couches natives ou des assertions de rendu retenues.

## Limites et suite

L'écart observé par le joueur entre le mode normal et spectateur n'a pas été profilé sur sa configuration : son origine reste une hypothèse. La baisse mesurée dans Nexus est réelle, mais n'attribue pas les autres ralentissements à ce seul mod.

Aucun test complet de combat multijoueur, de tous les sorts ou de toutes les armures externes n'a été réalisé dans cette passe. Leurs intégrations sont conservées dans les fichiers inchangés.

La prochaine optimisation de troisième personne devra mesurer les recherches de volumes voisins et les soustractions. PRE31 préserve volontairement cette découpe afin de conserver les raccords déjà fonctionnels.

Les sources, les données brutes et les classes compilées du correctif sont conservées dans la référence PRE31 du projet. L'assembleur permet de reproduire le JAR exact depuis PRE30 et vérifie sa somme.
