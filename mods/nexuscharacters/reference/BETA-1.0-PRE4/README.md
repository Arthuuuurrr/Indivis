# NexusCharacters Haute Capitale — BETA 1.0 PRE4

PRE4 développe la barbe comme élément culturel à part entière, sans modifier le schéma réseau ni le DTO de personnage.

Runtime artifact: `NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE4.jar`

SHA-256: `b8fc3cd3fb24803f4d64b0fa6dc574e343751d1a9678bd0360aaa5e7ea0fe0a5`

Source bundle: `NexusCharacters-HauteCapitale-BETA-1.0-PRE4-source.zip`

Source SHA-256: `9a70e279d12f51318525dcc3eec9eb095eebe9dc1b236f8f1fe13f68a5b4bf2e`

## Catalogue 3D

### Humains
- `trimmed` — Barbe taillée : compacte et angulaire.
- `noble_goatee` — Menton noble : moustache séparée et pointe centrale soignée.
- `long` — Barbe ajourée : modèle ouvert avec espaces visibles et trois mèches basses.

### Nordiques
- `short` — Barbe courte.
- `full` — Barbe pleine.
- `long` — Barbe ajourée.
- `wild` — Barbe sauvage : asymétrique, masses irrégulières.
- `split` — Barbe fendue : deux lobes bas séparés.

### Nains
- `short`, `full`, `long`, `forked`, `braided`.
- `double_braid` — Double natte.
- `triple_braid` — Triple tresse.
- `runic` — Barbe runique.
- `segmented` — Barbe segmentée.

## Ornements
Le bouton cosmétique existant devient `Ornement` lorsqu'une barbe 3D est sélectionnée :
- anneau de fer ;
- anneau de bronze ;
- anneau d'argent ;
- anneau d'or ;
- perle runique ;
- pince gravée ;
- embout de tresse.

Les restrictions dépendent de la race et du style de barbe. Les Nains disposent de la palette la plus large.

## Persistance
Aucun champ réseau n'est ajouté. La chaîne existante `beardStyle` encode `style#couleur~ornement`.

Exemple : `double_braid#brown~gold_ring`.

Les anciennes chaînes `full#brown`, `long#black`, etc. restent lisibles.

## Couleur
`Couleur : Liée aux cheveux` reste le mode par défaut. La même palette RGB que les cheveux est utilisée pour les barbes 3D. Les ornements sont rendus séparément avec leur teinte métallique/minérale.

## Validation statique
- JAR: archive valide.
- 84 textures de preview bijoux attendues présentes.
- Toutes les classes nouvelles et patchées sont lisibles par `javap`.
- Les principaux payloads et classes de persistance sont byte-identiques à PRE3.
- Tests de restrictions race/style/ornement passés.

Validation visuelle en jeu encore requise pour profils, morphologies, collisions armures/casques et position exacte des ornements.
