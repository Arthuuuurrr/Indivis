# CapSkills — état de référence TEST3

Version finale de cette séquence :

`capitale_skills_BETA_0_10_19_RC2F_TREE_HARD_RESTORE.zip`

SHA-256 :

`109ec0428909c009d6599637cc0b23a90c78bd84c9d5772c6447ecf01a3d34f4`

## Pourquoi RC2F

RC2D avait introduit des changements périphériques autour du Bâton de l’Avatar.
Après apparition d’un arbre vide, l’approche a été abandonnée.

RC2F restaure intégralement :

`data/capskills/puffish_skills/`

depuis RC2B, état structurel sain, tandis que le verrou Avatar est assuré côté
Haute Capitale RPG RC2.

## Invariants

- 549 JSON ;
- 338 skills ;
- 338 définitions ;
- 0 endpoint cassé ;
- composantes 321 + 17 ;
- la composante 17 = branche métiers/crafting volontairement séparée.

Validation :

```bash
python tools/validation/validate_capskills_tree.py /path/to/RC2F.zip
```
