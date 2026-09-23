# Indivis

Dépôt de travail du serveur Minecraft **L'Indivis / Haute Capitale**.

## Pilotage du travail

Le pilotage quotidien se fait avec les **Issues GitHub**, leurs priorités et les milestones.

- [P1 — priorité maximale](https://github.com/Arthuuuurrr/Indivis/issues?q=is%3Aissue+is%3Aopen+label%3A%22Priorit%C3%A9+maximale+-P1%22)
- [P2 — priorité secondaire](https://github.com/Arthuuuurrr/Indivis/issues?q=is%3Aissue+is%3Aopen+label%3A%22Probl%C3%A8me+prioritaire+secondaire+-P2%22)
- [P3 — à régler](https://github.com/Arthuuuurrr/Indivis/issues?q=is%3Aissue+is%3Aopen+label%3A%22A+r%C3%A9gler+-P3%22)
- [À tester / valider](https://github.com/Arthuuuurrr/Indivis/issues?q=is%3Aissue+is%3Aopen+label%3A%22A+tester+%2F+Valider%22)
- [Milestones](https://github.com/Arthuuuurrr/Indivis/milestones)

## Organisation

- `mods/` : JAR serveur de référence, tous regroupés directement dans ce dossier ; les rares sources de référence conservées restent dans leurs sous-dossiers.
- `datapacks/` : datapacks développés pour le serveur.
- `docs/` : architecture, inventaires, spécifications et tests.
- `.github/` : workflows et templates GitHub.
- `server-manifest.yml` : état déployé + versions de référence.

## Binaries

Les JAR sont stockés avec **Git LFS**. Quand les octets exacts sont disponibles, leur SHA-256 est enregistré dans le manifest. Si une version serveur exacte n'est pas disponible, elle reste absente plutôt que d'être remplacée par une version approximative.
