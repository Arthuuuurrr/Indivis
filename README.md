# Indivis

Dépôt de travail du serveur Minecraft **L'Indivis / Haute Capitale**.

## Pilotage du travail

Le pilotage quotidien ne se fait plus depuis un fichier roadmap. Les **Issues GitHub** sont la file de travail réelle, avec priorité, domaine, statut de validation et assignee.

- [P1 — priorité maximale](https://github.com/Arthuuuurrr/Indivis/issues?q=is%3Aissue+is%3Aopen+label%3A%22Priorit%C3%A9+maximale+-P1%22)
- [P2 — priorité secondaire](https://github.com/Arthuuuurrr/Indivis/issues?q=is%3Aissue+is%3Aopen+label%3A%22Probl%C3%A8me+prioritaire+secondaire+-P2%22)
- [P3 — à régler](https://github.com/Arthuuuurrr/Indivis/issues?q=is%3Aissue+is%3Aopen+label%3A%22A+r%C3%A9gler+-P3%22)
- [À tester / valider](https://github.com/Arthuuuurrr/Indivis/issues?q=is%3Aissue+is%3Aopen+label%3A%22A+tester+%2F+Valider%22)
- [Toutes les issues ouvertes](https://github.com/Arthuuuurrr/Indivis/issues?q=is%3Aissue+is%3Aopen)

`ROADMAP.md` et `docs/EXECUTION_ROADMAP.md` restent des documents d'orientation, pas des listes de tâches à maintenir manuellement.

## Organisation

- `mods/` : sources ou références source des mods développés/maintenus.
- `datapacks/` : datapacks développés pour le serveur.
- `docs/` : architecture, inventaires, spécifications et tests.
- `artifacts/` : politique des artefacts compilés.
- `.github/` : templates d'issues/PR.
- `server-manifest.yml` : état déployé + sources de référence.
- `docs/RUNTIME_SNAPSHOT_2026-09-22.md` : inventaire du serveur/client constaté sur captures.

## Workflow

1. issue priorisée ;
2. assignee ;
3. branche dédiée ;
4. développement ;
5. Pull Request ;
6. test/validation ;
7. merge ;
8. déploiement.

`main` doit rester stable et validée.

## Convention rapide des branches

- `fix/<issue>-<sujet>`
- `feat/<issue>-<sujet>`
- `refactor/<issue>-<sujet>`
- `docs/<sujet>`
- `chore/<sujet>`

## Convention des commits

`type(scope): description`

Exemple : `fix(skills): prevent spell reset on reconnect`.

## Binaries

Les JAR ne remplacent pas les sources. Quand les bytes exacts sont disponibles, leur SHA-256 est enregistré dans le manifest. Quand ils ne sont visibles que sur une capture, le manifest le signale explicitement.
