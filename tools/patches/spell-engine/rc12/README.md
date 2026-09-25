# Spell Engine TEST3 RC12 — HUD / Impact Restore

RC12 corrige deux régressions observées en runtime avec RC11 sans réécrire les systèmes déjà validés.

## Cause runtime

Le crash serveur du météore du 23/09/2026 est un `java.lang.VerifyError` dans `SpellImpacts.performImpact()`, atteint depuis `SpellProjectile.finishFalling()`. RC11 avait réécrit cette classe pour appliquer le facteur Hazenn de temps d'incantation aux impacts canalisés.

RC12 restaure donc `SpellImpacts.class` **byte-for-byte depuis RC9**.

## Position HUD validée

Les derniers screens/runtime validés plaçaient la spellbar à la même hauteur que la hotbar vanilla, légèrement décalée vers la gauche :

- origine : `BOTTOM`
- x : `-185`
- y : `-11`

RC11 avait réintroduit `y=-34`, ce qui faisait flotter la barre au-dessus. RC12 restaure `HudConfig.class` depuis RC9 puis étend la migration afin que l'ancienne position RC11 `(-185,-34)` soit automatiquement ramenée à `(-185,-11)`.

Positions migrées :
- `(-170,-34)` → `(-185,-11)`
- `(-170,-11)` → `(-185,-11)`
- `(-185,-34)` → `(-185,-11)`

Les positions personnalisées ne sont pas modifiées.

## Ce qui reste de RC11

- `ClientCastController.class` reste byte-for-byte identique à RC9.
- `SpellParameters.class` reste byte-for-byte identique à RC11 afin de conserver la séparation cast-time / cooldown.
- Spell Power RC9 reste le compagnon attendu.
- Aucun JSON de sort, mapping d'ability, icône ou ressource de gameplay n'est modifié.

## Reconstruction

```bash
python3 build_rc12.py \
  --rc9 spell_engine-RC9.jar \
  --rc11 spell_engine-RC11.jar \
  --out spell_engine-RC12.jar
```

Java 21 est requis pour le patch ASM de migration.

## Validation

Voir `docs/spell-integration/archive/VALIDATION_SPELL_ENGINE_RC12_HUD_IMPACT_RESTORE.txt`.

Statut : validation statique PASS ; validation Minecraft runtime requise avant STABLE.
