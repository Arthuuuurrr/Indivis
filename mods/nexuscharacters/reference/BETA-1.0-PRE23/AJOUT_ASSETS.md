# Ajouter des cosmétiques à la PRE23

Le catalogue recherche les PNG dans les ressources `nexuscharacters` actives. Les nouveaux fichiers suivent le même traitement 3D que les cosmétiques existants : il n'est pas nécessaire d'ajouter du code Java pour chaque coiffure, barbe classique ou tenue.

## Emplacements et noms

| Catégorie | Dossier dans le JAR ou le pack de ressources | Exemple de nouveau fichier | Identifiant |
|---|---|---|---|
| Coiffure | `assets/nexuscharacters/appearance_parts_v064/hair/` | `hair_14.png` | 14 |
| Cheveux longs | même dossier | `hair_long_09.png` | 14 |
| Cheveux courts | même dossier | `hair_short_06.png` | 1006 |
| Barbe ou moustache classique | `assets/nexuscharacters/appearance_parts_v068/facial_hair/` | `facial_07.png` | 7 |
| Tenue | `assets/nexuscharacters/appearance_parts_v064/outfits/` | `outfit_39.png` | 39 |

Choisir un identifiant libre. `hair_14.png` et `hair_long_09.png` sont deux façons de nommer la même sélection 14 : utiliser une seule des deux. Pour les prochains ajouts, incrémenter le numéro sans remplacer les fichiers existants. Les noms explicites `hair_N.png` et `facial_N.png` donnent des identifiants stables.

## Format des textures

Utiliser le gabarit Minecraft moderne 64 × 64, avec les mêmes zones UV que les fichiers de base. Un PNG carré dont la taille est un multiple de 64 est accepté et ramené au gabarit 64 × 64 par le compositeur. Conserver la transparence en dehors du cosmétique et les détails dessinés sur la seconde couche.

Les pixels de la couche supérieure produisent les voxels natifs de 3D Skin Layers. Les pixels cosmétiques présents seulement sur la couche inférieure sont également traités. Quand un asset possède déjà des détails supérieurs sur une partie du corps, cette empreinte inférieure reste moins épaisse pour conserver la silhouette et les mèches, cols, boutons ou plis existants. Les barbes déjà modélisées en 3D conservent leur modèle.

## Distribution

Pour intégrer un asset au mod par défaut, ajouter son PNG au chemin indiqué dans le JAR ou aux ressources de la prochaine compilation. Pour distribuer les ajouts sans modifier le JAR, utiliser un pack de ressources contenant ces mêmes chemins et un `pack.mcmeta` adapté à Minecraft 1.21.11.

Redémarrer Minecraft ou recharger les ressources avec F3 + T. Le menu parcourt alors les nouvelles entrées. Chaque client doit disposer des mêmes fichiers ; le serveur transmet les choix du personnage. Les restrictions de races des barbes classiques restent celles du mod d'origine.

## Vérification effectuée

Le module de test ajoute deux coiffures (`hair_long_09.png`, `hair_short_06.png`), deux barbes classiques (`facial_07.png`, `facial_09.png`) et une tenue (`outfit_39.png`) sans modifier le catalogue Java. Ces cinq fichiers sont des ressources du module de test, pas des cosmétiques supplémentaires imposés dans le JAR livré. Leur découverte et leur géométrie sont vérifiées avec les mêmes contrôles que les assets d'origine.
