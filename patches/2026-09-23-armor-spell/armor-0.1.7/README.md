# Armor Balance 0.1.7 — Mixin metadata fix

## Crash 0.1.6

Client et serveur échouaient avant l'initialisation avec :

`ArmorMaterialAttributeMixin ... missing an @Mixin annotation`.

L'inspection du JAR 0.1.6 a montré que la logique Java était présente, mais que `@Mixin` avait été émise dans `RuntimeVisibleAnnotations` par le stub de compilation temporaire. Les mixins fonctionnels du projet utilisent `RuntimeInvisibleAnnotations`, conformément à la rétention CLASS de `@Mixin`.

## Correctif 0.1.7

Aucune logique d'armure n'est modifiée.

Le mixin conserve :
- cible : `net.minecraft.class_1741` = ArmorMaterial ;
- méthode : `method_63993` = createAttributeModifiers(EquipmentType) ;
- injection : RETURN ;
- cancellable = true ;
- require = 1.

Seules les métadonnées sont corrigées :
- `@Mixin(..., remap=false)` en RuntimeInvisibleAnnotations ;
- `@Inject(..., remap=false)` en RuntimeVisibleAnnotations.

## Non-régression byte-for-byte

Identiques entre 0.1.6 et 0.1.7 :
- RuntimeArmorScaler.class
- RuntimeArmorScaler$Reflection.class
- Armor40DamageFormulaMixin.class
- CapitaleArmorBalance.class

Seuls les fichiers fonctionnels suivants changent :
- fabric.mod.json : version/description ;
- ArmorMaterialAttributeMixin.class : métadonnées Mixin.

## Artefact

`capitale_armor_balance-0.1.7+1.21.11-ARMOR40-MIXIN-METADATA-FIX.jar`

SHA-256:
`aeaaa04e7c44673c2686293bc53d5621258058e56e0972e468f4cb40a1882c8b`

Spell Engine reste RC17, inchangé :
`spell_engine-fabric-1.10.5.001+1.21.11-HC-TEST3-RC17-RPG-ARMOR-X2.jar`

SHA-256:
`64e31c2239b2e341c5ae514ca522814f36549c819fa88a7e7bf7a5186bc75fbd`

## Validation attendue

1. client et serveur démarrent ;
2. vanilla reste x2 ;
3. Armory Justicar = 40 ;
4. Bake Kujira = 26 ;
5. un set Death Knights est vérifié ;
6. toughness, max health, spell power, dégâts/vitesse d'arme restent inchangés.
