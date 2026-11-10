# PQ04/PQ05 R1 HF1 — liaison et interaction de Colin

## Problème constaté

Les logs fournis montrent l'échec de `capitale:pq45/liaison/colin`, puis des ouvertures de l'ancien dialogue de Colin sans page PQ04/PQ05. La sélection R1 utilise une boîte de recherche de quatre blocs en X/Z mais de 32 blocs dans chaque direction verticale. Elle exige ensuite qu'un seul EasyNPC soit présent dans cette boîte. Le message de « rayon de quatre blocs » est donc trompeur ; il ne distingue pas Colin d'autres PNJ.

La liaison administrative R1 ne réécrit pas les actions du PNJ public. Une action qui ouvre encore son dialogue générique peut donc continuer à le faire. Colin n'était pas reconnu par son seul nom dans la recherche des sources PQ04/PQ05.

## Modification

- Distance réelle de quatre blocs pour la liaison, reconnaissance par identité, et commande administrative facultative `indivispq45 bind_colin <UUID>`.
- Refus conservé pour les ambiguïtés et les copies privées. Liaison inchangée autorisée ; une scène utilisant une autre source de Colin doit être suspendue avant un changement.
- Recherche de source étendue au nom normalisé, aux tags et aux anciennes commandes. Le clic ciblé mémorise directement le PNJ public cliqué en l'absence de liaison.
- Phase Fabric dédiée placée avant les interactions par défaut, limitée au Colin public reconnu, aux personnages après la remise de Roch et aux étapes compatibles. L'entrée est différée par le moteur R1 existant ; l'ancien dialogue n'est pas ouvert pour ce clic.
- Le compteur de conversation du journal et sa protection d'usage d'objet sont relayés. Les autres PNJ, les autres quêtes, les copies privées, l'autre main et l'édition accroupie gardent leur chemin existant.
- Les graphes de dialogues, les transactions, les scores de progression, les sauvegardes Nexus, le moteur de déplacement et les deux datapacks restent identiques à R1.

## Reproduire la construction

Linux, Python 3, Java 21 avec module `jdk.compiler`, accès réseau pour les dépendances officielles.

1. Télécharger l'archive complète R1 depuis le commit `a9702285919a48eb26427ab2967fd7f67d78984d`, chemin `livraisons/Indivis_PQ04_PQ05_R1_Installation_Complete.zip` du dépôt `Arthuuuurrr/Indivis`.
2. Exécuter, depuis le dossier de ces sources :

```bash
python3 build_hf1.py --r1 /chemin/Indivis_PQ04_PQ05_R1_Installation_Complete.zip --workdir /chemin/atelier --test
```

Le script vérifie l'empreinte du JAR de base, récupère les outils Minecraft/Fabric via les scripts R1, compile uniquement `src/Pq45Runtime.java`, puis produit le JAR et un manifeste différentiel dans `output/` et `reports/`. Les classes de test ne sont jamais intégrées au JAR livré. Le test télécharge l'EasyNPC 7.16.0 officiel et en vérifie l'empreinte, crée un monde plat jetable, puis arrête le serveur après ses contrôles.

## Limites des tests

Le banc utilise Minecraft 1.21.11, Fabric 0.19.5, Fabric API 0.141.6 et le véritable EasyNPC 7.16.0. La connexion du joueur, le personnage sélectionné et l'absence de dialogue ouvert sont synthétiques. Pour isoler la sélection et le routage, les autres points d'entrée et mixins du mod sont désactivés **dans une copie de test seulement** ; les classes testées proviennent du JAR livré. Le test vérifie la file d'entrée PQ04 et l'ordre réel des callbacks Fabric, sans jouer l'escorte ni rendre l'écran de dialogue. Les contrôles R1 de l'escorte restent ceux de la livraison précédente ; aucun nouveau test visuel, sur la vraie carte ou de FPS n'est revendiqué.
