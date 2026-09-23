# Armor40 + Spell Engine — correctifs du 23/09/2026

Branche de travail pour deux corrections liées au test runtime du 23 septembre 2026.

## 1. Capitale Armor Balance 0.1.2

Artefact livré :
`capitale_armor_balance-0.1.2+1.21.11-ARMOR40-X2-ALL-ARMOR.jar`

SHA-256 :
`5be8d6e7f2a1036733c178c3c12977686c2eec0a864b4280824cc8713fcf9f5e`

Objectif :
- doubler uniquement les valeurs `generic.ARMOR` en `ADD_VALUE` ;
- inclure désormais les armures vanilla et moddées ;
- ne pas modifier les armes ;
- laisser inchangés toughness, max health, knockback resistance, Spell Power et les autres attributs.

Le filtre reste limité aux slots FEET / LEGS / CHEST / HEAD / ARMOR / BODY. Si un item ne possède aucun modifier ARMOR correspondant, son composant d'attributs n'est pas réécrit. Les dégâts/vitesse d'attaque des armes ne sont donc pas modifiés.

La formule ARMOR40 est inchangée par rapport à 0.1.1 : entrée dégâts x2, plafond d'armure 40, diviseur 50, résultat x0.5.

Delta 0.1.1 -> 0.1.2 :
- `fabric.mod.json`
- `fr/hautecapitale/armorbalance/CapitaleArmorBalance.class`

## 2. Spell Engine RC15

Artefact livré :
`spell_engine-fabric-1.10.5.001+1.21.11-HC-TEST3-RC15-WEAPON-SPELL-RESTORE.jar`

SHA-256 :
`b382079d8e3b240ee83a8c66f71f3d139901c04a351b22e1b9cb5e3f60b4d515`

Symptôme après RC14 :
les spells restent présents dans le datapack mais ne s'affichent plus correctement avec les armes magiques ou martiales.

Cause ciblée :
RC14 sélectionnait le chemin d'icône « item-use » dès que `slot.itemStack() != null`. Or un slot d'arme peut porter à la fois un ItemStack et un vrai spell. Dans ce cas le chemin normal `SpellRender.iconTexture(spellId)` pouvait être court-circuité.

Correction RC15 :
- si `spell != null` -> toujours le rendu normal du spell ;
- sinon, si `itemStack != null` -> fallback RC14 `HcItemUseSpellIcon` ;
- le correctif d'icône clic droit RC14 est donc conservé pour les vrais slots item-use.

Delta RC14 -> RC15 :
- `fabric.mod.json`
- `net/spell_engine/client/gui/HudRenderHelper.class`

Aucun JSON de spell, cooldown, cast, impact, input ou position HUD n'a été modifié.

## Statut

Validation statique effectuée :
- intégrité ZIP : PASS ;
- delta contrôlé : PASS ;
- bytecode RC15 : branche `spell != null` prioritaire confirmée ;
- bytecode Armor 0.1.2 : exclusion `minecraft:` retirée, formule de dégâts inchangée.

Validation runtime encore requise avant promotion dans la bibliothèque `artifacts/jars`, qui reste réservée aux JAR réellement déployés sur le serveur de référence.

Tests runtime :
1. démarrage client + serveur ;
2. plastron diamant vanilla 8 -> 16, set complet 20 -> 40 ;
3. armures moddées toujours x2 ;
4. dégâts/vitesse des armes inchangés ;
5. spells magiques et martiaux de nouveau visibles ;
6. Décapitation clic droit conserve son icône.
