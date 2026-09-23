# Armor Balance 0.1.4 — startup crash fix

Le test 0.1.3 a provoqué le même crash client et serveur :

`ItemStackArmorAttributeMixin` ciblait `net.minecraft.class_1799.method_58695`, méthode absente en Minecraft/Fabric 1.21.11.

## Correction

La 0.1.4 supprime entièrement ce mixin ItemStack.

Le runtime passe désormais par les méthodes 1.21.11 vérifiées de `AttributeModifiersComponent` (`net.minecraft.class_9285`) :
- `method_57482(EquipmentSlot, BiConsumer)`
- `method_60618(AttributeModifierSlot, BiConsumer)`
- `method_70727(AttributeModifierSlot, TriConsumer)`

Les injections ont `require = 0` : une divergence future de mapping ne doit plus rendre le démarrage critique.

## Invariants

Toujours x2 uniquement pour :
- attribut ARMOR ;
- opération ADD_VALUE ;
- slot FEET / LEGS / CHEST / HEAD / ARMOR / BODY.

Inchangés :
- MAINHAND / OFFHAND ;
- dégâts et vitesse d'attaque ;
- toughness ;
- max health ;
- Spell Power ;
- modificateurs multiplicatifs.

Le composant original n'est jamais muté ; la vue x2 est temporaire.

## Validation statique

- `method_58695` absent du JAR 0.1.4.
- Les trois méthodes ciblées sont confirmées dans les mappings Fabric/Yarn 1.21.11.
- Le hash de `Armor40DamageFormulaMixin.class` est identique entre 0.1.3 et 0.1.4.
- Harness : ARMOR chest 8 -> 16, ARMOR MAINHAND 3 -> 3, autre attribut 5 -> 5, source reste 8.
- ZIP/JAR integrity : PASS.

Artefact :
`capitale_armor_balance-0.1.4+1.21.11-ARMOR40-COMPONENT-RUNTIME-SAFE.jar`

SHA-256 :
`3f868542ce33d4fa14c948a42d8e63bef18687d50bb8c2af2e6df8aa1a21e47a`

RC16 Spell Engine n'est pas modifié : les logs de crash attribuent l'échec au seul mixin 0.1.3 Armor Balance.
