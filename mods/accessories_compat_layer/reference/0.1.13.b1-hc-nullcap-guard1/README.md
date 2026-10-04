# Accessories Compatibility Layer 0.1.13.b1 — HC NULLCAP GUARD1

Issue principale : #16

## Symptôme

Le serveur spamme lors du chargement NBT d'entités :

```text
Exception loading entity
Caused by: java.lang.NullPointerException:
Cannot invoke "io.wispforest.accessories.api.AccessoriesCapability.entity()"
because "capability" is null

at io.wispforest.accessories.impl.core.AccessoriesHolderImpl.getHolder(...)
at io.wispforest.accessories_compat.trinkets.wrapper.WrappedTrinketComponent.readData(...)
```

## Cause confirmée

Dans le JAR 0.1.13.b1 :

`WrappedTrinketComponent.readData` appelle directement :

```java
AccessoriesHolderImpl.getHolder(capability())
```

Or `AccessoriesCapability.get(entity)` est explicitement nullable.

Dans Accessories, `LivingEntityMixin.accessoriesCapability()` retourne `null` lorsque
`EntitySlotLoader.getEntitySlots(entity)` est vide.

En parallèle, le compat layer enregistre `WrappedTrinketComponent` via Cardinal Components
pour les `LivingEntity`.

On peut donc avoir :

```
LivingEntity
 -> composant Trinkets wrapper présent
 -> aucun slot Accessories pour cette entité
 -> AccessoriesCapability == null
 -> readData appelle getHolder(null)
 -> NPE
```

## Correctif de confinement

Build candidate :

`accessories_compat_layer-fabric-0.1.13.b1+1.21.11-HC-NULLCAP-GUARD1.jar`

SHA-256 :

`5134c271aa455c6fd71ad71d7bdcb3379ffdd921364b8709f7847516094802c8`

Base :

`accessories_compat_layer-fabric-0.1.13.b1+1.21.11.jar`

SHA-256 :

`a6d2944deceb90b795d96abbc08d8c4d138c27898dde3e2a5f4eff144437c928`

### Modification exacte

Deux méthodes uniquement reçoivent un garde au tout début :

```java
if (capability() == null) return;
```

Méthodes :
- `WrappedTrinketComponent.readData(ReadView)`
- `WrappedTrinketComponent.writeData(WriteView)`

Le garde `writeData` évite le même chemin invalide au moment de la sauvegarde d'une entité
qui n'a aucun slot/capability Accessories.

## Ce qui n'est pas modifié

- aucun slot `ring` / `necklace` ;
- aucune configuration MMO Accessories ;
- aucun item MMO Accessories ;
- aucune classe Accessories core ;
- aucune classe Cardinal Components ;
- aucun mixin d'enregistrement ;
- aucune logique d'équipement/déséquipement ;
- aucune logique de lifesteal ;
- aucune migration pour les entités qui ont une capability valide.

Pour une entité dont `capability() != null`, le corps original est exécuté tel quel.

## Limite volontaire

C'est un **hotfix de confinement**, pas la résolution architecturale finale.

Si une LivingEntity possède un ancien payload Trinkets mais n'a actuellement **aucun slot Accessories**,
ce payload n'est pas migré par le wrapper pendant ce chargement.

C'est préférable au NPE répété, mais Clément doit décider ensuite de la politique définitive :
- ne pas enregistrer le wrapper sur les entités sans slots ;
- différer la lecture jusqu'à disponibilité de la capability ;
- préserver explicitement les données orphelines ;
- ou autre solution upstream-compatible.

Ne pas généraliser ce `return` à des entités qui ont normalement des slots sans comprendre pourquoi
leur capability serait absente.

## Validation statique

- JAR intègre : `unzip -t` OK ;
- aucune signature JAR à invalider ;
- archive diff :
  - modifié : `fabric.mod.json`
  - modifié : `WrappedTrinketComponent.class`
  - ajouté : `HC_PATCH_NOTES_NULLCAP_GUARD1.txt`
- `readData` contient bien le garde avant `AccessoriesHolderImpl.getHolder` ;
- `writeData` contient le même garde ;
- aucune autre classe modifiée.

## Validation runtime à faire

- [ ] démarrage serveur sans erreur liée au compat layer ;
- [ ] charger la zone qui produisait le spam ;
- [ ] confirmer disparition du WARN `Exception loading entity` ;
- [ ] vérifier qu'une entité normale sans slots Accessories reste fonctionnelle ;
- [ ] connexion joueur avec accessoires déjà équipés ;
- [ ] vérifier bagues/colliers et autres slots visibles ;
- [ ] déco/reco avec accessoires équipés ;
- [ ] arrêt/redémarrage serveur ;
- [ ] vérifier qu'aucun accessoire joueur n'est perdu ;
- [ ] vérifier absence de nouveau WARN à la sauvegarde.

## Reprise par Clément

Le correctif final de #16 doit continuer séparément :
- slots `ring` / `necklace` visibles ;
- migration des anciens équipements fantômes ;
- déséquipement fiable ;
- suppression immédiate du lifesteal après retrait ;
- compatibilité Accessories / Trinkets propre.

Ce hotfix ne doit pas être présenté comme résolvant l'ensemble de #16.
