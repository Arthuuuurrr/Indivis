# PRE24 — raccords des vêtements

La validation visuelle de la PRE23 est retirée : ses contrôles de chevauchement passaient alors que les vêtements avaient des raccords décalés. La PRE24 corrige leur construction et fournit une comparaison avec les textures originales et le rendu natif de Skin Layers.

## Modification

Les deux couches des vêtements utilisent désormais la même grille UV. L'épaisseur change suivant la normale de la surface, sans déplacer les limites intérieures des pixels. Le tissu a une profondeur constante de 0,10 pixel de modèle ; les détails supérieurs suivent la profondeur native, avec un minimum de 0,25 pixel. Leurs flancs descendent jusqu'au tissu. Les volumes utilisés pour supprimer les intersections correspondent aux faces fermées réellement produites.

La vue à la première personne reconstruit ce profil sur la grille des bras. Elle ne comprime plus le tissu en appliquant une réduction globale au vêtement. Les cheveux et barbes classiques gardent leur générateur natif ; les barbes déjà modélisées gardent leur modèle.

## Résultats du JAR final

| Contrôle | Résultat |
|---|---|
| Grille des tenues 1, 20, 22, 23 | Déplacement intérieur mesuré : 0 pixel |
| Même contrôle sur la PRE23 rejetée | Échec détecté sur les quatre tenues ; déplacement jusqu'à 0,178 pixel |
| Deux pixels adjacents de hauteurs différentes | Flanc raccordé, aire attendue, trou de détail conservé, UV du pixel source |
| Catalogue : 57 configurations × 3 poses | 171 poses, aucun chevauchement coplanaire ni croisement détecté |
| Joueur réel : 12 configurations × 5 poses | 60 poses, aucune surface manquante, aucun chevauchement détecté |
| Aperçus du menu, bras larges et fins | 12 contrôles géométriques, 24 captures, aucun chevauchement détecté |
| Profils des bras à la première personne | 48 contrôles ; grille alignée et épaisseur du tissu conservée |
| Comparaison des vêtements | 48 captures PRE24 et 48 captures PRE23 ; trois angles distincts pour chaque variante |
| Assets de la PRE17 | 4 958 fichiers identiques, octet pour octet |
| Assemblage depuis le paquet de référence | JAR identique au JAR testé |

La comparaison utilise la même texture composée et la même pose : variante 0 = version du mod, 1 = couche supérieure d'origine rendue nativement, 2 = tous les pixels au relief natif, 3 = surfaces avant découpage. Les variantes 1 à 3 sont des références du module de test. Voir `evidence/comparaison-vetements.png` et `evidence/vetements-quatre-tenues.png`.

Les captures utilisent Minecraft 1.21.11, Fabric, Skin Layers 1.10.2, Sodium, ImmediatelyFast, EntityCulling, PAL et le HUD de la Capitale. Le moteur OpenGL fonctionne ici via OSMesa ; ces résultats ne constituent pas une mesure de FPS sur la machine de l'utilisateur. Le journal du joueur réel mesure 16,06 ms en moyenne par reconstruction et 225,05 ms au maximum dans ce banc de test. Le test de skin de compte utilise une texture de compte mise en cache, sans authentification Mojang en direct.

Les nouveaux PNG de test comprennent deux coiffures, deux barbes et une tenue, découverts sans ajout de code au catalogue. Ils restent dans le module de test et ne sont pas imposés comme nouveaux assets dans le JAR distribué. Les ajouts au mod suivent `AJOUT_ASSETS.md`.

## Fichiers et reproduction

JAR : `NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE24.jar`, 21 601 939 octets.

SHA-256 : `abeb2a1135f9df7fc723adf24c97d2dce589267a420e375aff6148f3d9597f82`.

`build-manifest.json` identifie les sources, les classes et les preuves. `compiled-patches.zip.b64` contient les classes nécessaires à l'assemblage depuis la PRE23. Le workflow reconstruit et conserve exactement ce binaire ; les tests Minecraft ont été exécutés dans le banc local, pas dans le workflow d'assemblage.

Pour vérifier les preuves archivées, placer le JAR livré sous `candidate.jar` dans ce dossier, extraire les deux archives de captures dans `evidence`, puis exécuter `python final_check.py` avec Pillow installé. `verified.log` préserve le journal complet retenu pour chaque exécution ; `verified-log-source.json` indique son fichier source et son empreinte.

Les sources Java des tests, les fixtures et les scripts du banc sont conservés. Ils supposent un environnement de test Minecraft 1.21.11 remappé en intermediary, Java 21 et les dépendances du banc PRE23. Les contrôles géométriques ne remplacent pas l'inspection visuelle : les quatre tenues ci-dessus ont aussi été examinées de trois quarts, de profil et de dos, puis dans les combinaisons avec cheveux et barbes capturées.
