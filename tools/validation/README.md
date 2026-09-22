# Outils de validation

## CapSkills RC2F

```bash
python tools/validation/validate_capskills_tree.py /chemin/capitale_skills_BETA_0_10_19_RC2F_TREE_HARD_RESTORE.zip
```

Vérifie notamment :

- intégrité ZIP ;
- parse de tous les JSON ;
- 549 JSON ;
- 338 skills / 338 définitions ;
- endpoints de connexions ;
- doublons de connexions ;
- composantes 321 + 17 ;
- présence de la branche métiers de 17 nœuds ;
- 265 JSON d’abilities.

## Bundle TEST3 final

Extraire le bundle, puis :

```bash
python tools/validation/validate_spell_integration_artifacts.py /chemin/HAUTE_CAPITALE_FINAL_MODS_RC7_HAZENN/
```

Vérifie :

- SHA-256 exacts ;
- intégrité des JAR/ZIP ;
- absence d’entrées ZIP dupliquées ;
- classes critiques attendues ;
- absence du mixin Spell Power RC6 fautif ;
- présence des bridges TEST3 finaux.

Ces scripts sont **statiques**. Ils ne remplacent pas les tests Minecraft de `docs/spell-integration/RUNTIME_MATRIX.md`.
