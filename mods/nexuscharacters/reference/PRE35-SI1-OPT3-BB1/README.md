# Nexus OPT3 + modèles Blockbench BB1

Ajout à la version Minecraft 1.21.11 / Fabric, sans remplacement des anciens assets.
Suivi : issue #185. Base fonctionnelle OPT3 : travail référencé par la PR #183.
Cette référence est distincte de l'ancienne base PRE17 du dépôt ; ne pas reconstruire depuis PRE17.

## Construire

1. Java 21, Python 3, libarchive.so.13, accès aux dépendances Mojang/Maven/Modrinth.
   Pillow et DejaVu Sans sont nécessaires uniquement à la génération des planches de contrôle.
2. Fournir dans `base/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3(1).jar`
   le binaire OPT3 de SHA-256 `de06ecab0f5cf7ccee29043bff2ccb5fd2b7425c173da145c1f27559b49def7b`.
3. Extraire l'archive fournie :
   `python3 tools/unpack_rar.py inputs/JSON-BLOCKBENCH-COIFFURES-BARBES-OREILLES.rar --extract input`
4. Restaurer les dépendances avec `python3 bootstrap.py`, puis `python3 build.py`.

Le générateur valide les géométries/UV/PNGs et produit un JAR ainsi qu'un CSV de répartition.
Il refuse toute modification des 4 966 entrées d'assets déjà présentes dans OPT3.
Les modèles et textures ajoutés sont embarqués octet pour octet, sans reconstruction de skin.

## Répartition et ajouts ultérieurs

- `distribution.json` : choix des races ; uniquement les nouveaux modèles sont filtrés.
- `hair_ids.json` : identifiants persistants des coiffures. Ne jamais réattribuer un ID existant.
- `materials.json` : palettes exactes des bijoux à préserver ; ne pas classer les métaux
  par un seuil RGB qui confondrait une barbe blonde avec de l'or.
- Nouveaux modèles : ajouter le couple geo.json/PNG, la distribution, le libellé dans `build.py`,
  et, pour une coiffure, un nouvel ID libre dans `hair_ids.json` (541 et suivants).
- Les styles de barbe/oreilles utilisent `bb_<nom>` (32 caractères maximum avant couleur).
- Regénérer et installer le JAR côté client ET serveur. La liaison EasyNPC reste inchangée.
- Les modèles sont compilés une fois en ModelParts vanilla ; les textures colorées sont mises
  en cache avec une limite de 256 variantes. Aucun chargement de JSON à chaque image.
- Les modèles fournis ne contiennent pas d'animation. Les futurs modèles doivent utiliser
  le même sous-ensemble validé : box UV, os imbriqués, rotations d'os, dilation par cube.
  Les rotations de cube et UV par face sont refusées explicitement, pas ignorées.

## Vérifications

`previous/npc/run_client.py` est un banc d'essai Minecraft réel isolé :
`NPC_EXTERNAL_DISPLAY=xorg NPC_DISPLAY=138 NPC_QA_MODE=blockbench NEXUS_TEST_JAR=<chemin absolu> python3 previous/npc/run_client.py`.

Le mode `blockbench` parcourt les 91 modèles de face/dos et vérifie leurs ModelParts natifs,
les restrictions raciales, les palettes et les caches. Le mode `bbnpc` vérifie l'export de
l'éditeur, l'application serveur et le rendu EasyNPC de cinq recettes utilisant les ajouts.
Le mode `menus` compare les anciens choix à `menus-baseline` sur le binaire OPT3.

`python3 verify_release.py` reconstruit puis exécute ces contrôles successivement, y compris
le redémarrage du monde isolé. `package_release.py` refuse une livraison dont les résultats
ne correspondent pas au hash exact du JAR final.

Les captures proviennent du client réel, pas d'un moteur de rendu de substitution.
Les mesures FPS d'un serveur réel ne sont pas déduites de ces tests logiciels.
Voir le rapport livré pour les résultats effectivement obtenus et le hash testé.

Le drapeau `-Dnexus.blockbench.diagnostics=true` active les compteurs détaillés du banc
d'essai ; les maps de diagnostics ne sont pas créées dans un lancement normal.
