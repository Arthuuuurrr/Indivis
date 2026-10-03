# Ajouter des cosmétiques à la PRE26

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

Les vêtements conservent une grille de pixels commune aux deux couches : le tissu inférieur a une épaisseur régulière et les détails supérieurs possèdent des flancs raccordés jusqu'au tissu. La profondeur de ces détails suit le profil de Skin Layers. Les cheveux et barbes classiques conservent leurs voxels natifs de 3D Skin Layers. Les pixels cosmétiques présents seulement sur la couche inférieure sont également traités. Pour les vêtements, l'épaisseur du tissu reste constante même si un détail supérieur est ajouté ailleurs. Pour les cheveux et barbes classiques, l'empreinte inférieure reste moins épaisse lorsqu'une partie possède déjà des détails supérieurs. Les barbes déjà modélisées en 3D conservent leur modèle.

## Distribution

Pour intégrer un asset au mod par défaut, ajouter son PNG au chemin indiqué dans le JAR ou aux ressources de la prochaine compilation. Pour distribuer les ajouts sans modifier le JAR, utiliser un pack de ressources contenant ces mêmes chemins et un `pack.mcmeta` adapté à Minecraft 1.21.11.

Redémarrer Minecraft ou recharger les ressources avec F3 + T. Le menu parcourt alors les nouvelles entrées. Chaque client doit disposer des mêmes fichiers ; le serveur transmet les choix du personnage. Les restrictions de races des barbes classiques restent celles du mod d'origine.

## Vérification effectuée

Le module de test ajoute deux coiffures (`hair_long_09.png`, `hair_short_06.png`), deux barbes classiques (`facial_07.png`, `facial_09.png`) et une tenue (`outfit_39.png`) sans modifier le catalogue Java. Ces cinq fichiers sont des ressources du module de test, pas des cosmétiques supplémentaires imposés dans le JAR livré. Leur découverte et leur géométrie sont vérifiées avec les mêmes contrôles que les assets d'origine.


## Disposition des bras (PRE26)

Les personnages composés par Nexus utilisent le modèle classique. Les sources de corps et de tenues dessinées sur le gabarit slim sont adaptées face par face avant la composition et la génération des couches 3D. Les PNG livrés restent inchangés. Les tenues slim livrées sont reconnues, ainsi que les nouvelles sources avec des manches pleines et la marge transparente caractéristique. Une texture avec seulement quelques bracelets ou détails peut être ambiguë.

Pour déclarer explicitement le gabarit de la source, placer un petit fichier à côté du PNG : `outfit_39.arm-layout.json` pour `outfit_39.png`, ou `body_heavy.arm-layout.json` pour `body_heavy.png`. Contenu : `{"arm_width": 3}` pour slim ou `{"arm_width": 4}` pour classique. Cette déclaration est prioritaire, notamment lorsqu'un pack remplace une texture slim existante par une texture classique. Garder le même préfixe de nom, le même dossier et la grille UV Minecraft ; les deux couches des bras sont adaptées ensemble.

L'adaptation concerne le gabarit UV des textures. Elle ne modifie pas les proportions raciales ni la morphologie définie par les curseurs. Le skin Mojang suit son propre modèle et n'est pas converti.
