# NexusCharacters — PRE35 SI1

Issue #178, branche `fix/nexus-pre35-player-render`, PR #179. Pas de fusion automatique.

La reconstruction fait suite à la suppression de l'espace temporaire pendant une
déconnexion. Les nombres du premier checkpoint sont historiques ; seuls les contrôles
présents dans le rapport final et accompagnés d'une empreinte du nouveau JAR concernent
la livraison reconstruite.

## Reproduire le binaire validé

Python 3, sans dépendance tierce :

```sh
python3 assemble.py /chemin/NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar /chemin/NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar
```

La base est refusée si son SHA-256 ne correspond pas à la PRE34 SI1 exacte.
Le patch compilé et chaque entrée sont vérifiés, puis le SHA-256 du JAR complet est contrôlé.

## Compiler et tester

- Temurin 21.0.13+9, JOML 1.10.8, base PRE34 SI1 ; `python3 build.py`.
- Runtime sous `../pre31`, bootstrap MC 1.21.11 / Fabric Loader 0.19.5.
- Copier les scripts de `runtime-tools/` dans `../pre31/tools/`, puis exécuter
  `bootstrap.py`, récupérer le profil Fabric dans `runtime/loader.json`, exécuter
  `loader_deps.py`, `dependencies.py` et `assets.py`.
- Les scripts de runtime d'origine sont disponibles au commit immuable
  `f1b0336faf49350321183e18206c6c4b541c7602`, dossier
  `mods/nexuscharacters/reference/BETA-1.0-PRE31/tools/`.
- `tools/more_dependencies.py` récupère les dépendances publiques exactes.
- Les JAR privés de compatibilité proviennent d'Arthur et ne sont pas redistribués.
- `tools/display.py` prépare un affichage de test local, sans installation système.
- `python3 tools/standalone.py` compare les méthodes originales copiées depuis la base SI1
  aux méthodes optimisées, sur les mêmes objets.
- `python3 run_client.py --baseline`, puis sans option : menus, mains, états de géométrie.
- `--future` : nouveaux PNG de cheveux/barbe/tenue, en corps 2, normal et slim.
- `--world` : monde jetable, 24 scènes, autres joueurs, canalisation et dialogue.
- `--world --oracle` : même monde, comparaison exacte de chaque nettoyage ancien/nouveau.
- `--perf --baseline` puis `--perf` : clients successifs, sans autre client actif.
- `tools/compare.py` et `tools/package.py` refusent une livraison incomplète.

Les preuves monde sont propres à chaque lancement, puis copiées dans des dossiers distincts
témoin/candidat/oracle. Le JAR oracle et les PNG de test ne sont jamais inclus dans la livraison.

Voir le rapport pour les limites : pas de 8 connexions réseau, pas de benchmark GPU sur
le PC d'Arthur, ni de reproduction de Lion-Port ou de l'ensemble du modpack.
