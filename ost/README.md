# OST — Indivis

Bibliothèque canonique des musiques utilisées par le serveur. Les fichiers audio sont conservés séparément du code de `MMOMusicZones` afin de garder une archive simple, traçable et réutilisable.

## Inventaire source

Lot de référence : `ost-20260924`

| Dossier | Fichiers | Nombre |
|---|---:|---:|
| `NATURE/` | `1.mp3` à `36.mp3` | 36 |
| `DONJON/` | `1.mp3` à `9.mp3` | 9 |
| `COMBAT/` | `1.mp3` à `16.mp3` | 16 |
| `BOSS/` | `1.mp3` à `14.mp3` | 14 |
| `VILLE/` | `1.mp3` à `41.mp3` | 41 |

**Total : 116 pistes — 605 860 946 octets (~577,79 Mio).**

Les numéros et catégories doivent rester inchangés pour préserver les références existantes. Ne pas importer plusieurs copies d'une même piste sous des noms différents.

## Git LFS

Les `.mp3` sont suivis par Git LFS via le fichier `.gitattributes`. Les fichiers audio ne doivent pas être remplacés par des pointeurs manuels ou des archives fragmentées : le dépôt doit conserver les vrais objets LFS.

## Affectations VILLE actuellement retenues

| Zone | Jour | Nuit |
|---|---|---|
| Capitale | 39 | 38 |
| Ilystara | 40 | 40 |
| Lion-Port | 36, 35 | 27 |
| Clairval | 37 | 18 |
| Haute-Rive | 41 | 15 |
| Sylvharen | 34, 1 | 8 |
| Skarnfjord | 33, 29 | 30, 24, 23 |
| Dûrak-Vor | 26, 25 | 22, 23 |
| Avaleiv | 5, 9 | 13 |

La configuration fonctionnelle de lecture et de zones reste documentée dans `docs/MMO_MUSIC_ZONES.md`.
