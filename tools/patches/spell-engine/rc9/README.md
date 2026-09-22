# Spell Engine TEST3 RC9 — HUD + Icon Sync

Ce dossier conserve les sources de patch/harness utilisées pour produire RC9.

## Entrées

- Spell Engine TEST3 RC7 HAZENN FULLCOMPAT ;
- CapSkills 0.10.19 RC2F TREE HARD RESTORE.

## 1. Générer le mapping d’icônes

```bash
python generate_capskills_icon_map.py \
  capitale_skills_BETA_0_10_19_RC2F_TREE_HARD_RESTORE.zip \
  hc_spell_icon_map.json \
  map.tsv
```

Invariants attendus :

- 216 mappings spell → texture ;
- 0 conflit ;
- 5 abilities visibles sans texture explicite.

## 2. HUD

`PatchHud.java` :

- défaut final `x=-185, y=-11` ;
- migration seulement de `(-170,-34)` et `(-170,-11)` ;
- positions custom préservées.

## 3. Icônes

`PatchSpellIcons.java` remplace `SpellRender.iconTexture(...)` par une
délégation à `HcSpellIconFallback.resolve(...)`.

Pour les 216 sorts mappés, l’icône CapSkills est utilisée. Pour les autres,
le comportement upstream est conservé.

## 4. Harness

`TestIcons.java` vérifie :

- mapping standard Fireball ;
- mapping dont texture != chemin du spell (`rend_boost_a`) ;
- fallback d’un sort inconnu.

## 5. Vérification structurelle

Avant distribution, les classes suivantes ont été passées à ASM
`CheckClassAdapter` :

- `SpellRender` ;
- `HcSpellIconFallback` ;
- `HudConfig` ;
- `HcHudMigration`.

Rapport : `docs/spell-integration/archive/VALIDATION_SPELL_ENGINE_RC9_HUD_ICON_SYNC.txt`.
