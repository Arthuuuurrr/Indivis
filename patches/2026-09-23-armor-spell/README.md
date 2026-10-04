# Armor40 + Spell Engine — correctifs du 23/09/2026

## Résultat du test runtime 0.1.2 / RC15

RC15 a restauré l'affichage des spells magiques et martiaux.

Le client signale encore une ressource absente :
`capitale:textures/spell/provocation.png`.

Armor Balance 0.1.2 est bien exécuté des deux côtés et journalise notamment Armory RPGs :
- Justicar chest : `8 -> 16`
- Justicar legs : `6 -> 12`
- Destroyer chest : `8 -> 16`

Malgré cela, les valeurs Armory observées en jeu restent celles d'origine, alors que les armures vanilla sont correctement doublées. Le passage sur les composants par défaut est donc insuffisant pour le chemin effectif utilisé par Armory.

## 1. Capitale Armor Balance 0.1.3 — runtime effective

Artefact candidat :
`capitale_armor_balance-0.1.3+1.21.11-ARMOR40-RUNTIME-EFFECTIVE.jar`

SHA-256 :
`f0bc03b2bb01ae78e7ea798e08014299dc801265cb9c048c4c594bf3cc311281`

Correction :
- abandon du rewrite ponctuel via `DefaultItemComponentEvents.MODIFY` ;
- interception de la valeur effective renvoyée par `ItemStack.method_58695` / `getOrDefault` ;
- transformation temporaire de `AttributeModifiersComponent` via MixinExtras `@ModifyReturnValue` ;
- aucune mutation du composant source, donc aucun cumul `x2 -> x4`.

Filtre inchangé :
- attribut = ARMOR ;
- opération = ADD_VALUE ;
- slot = FEET / LEGS / CHEST / HEAD / ARMOR / BODY.

Donc restent inchangés :
- MAINHAND / OFFHAND ;
- dégâts et vitesse des armes ;
- toughness ;
- max health ;
- Spell Power ;
- accessoires ANY ;
- modificateurs ARMOR multiplicatifs.

Le mixin de formule ARMOR40 est strictement identique à 0.1.2.

Harness :
- armor chest 8 -> 16 : PASS ;
- ARMOR MAINHAND 3 -> 3 : PASS ;
- ARMOR multiplicatif 2 -> 2 : PASS ;
- autre attribut 5 -> 5 : PASS ;
- lecture répétée : 16 puis 16, jamais 32 : PASS ;
- composant original : reste 8 : PASS.

## 2. Spell Engine RC16 — icône Provocation

Artefact candidat :
`spell_engine-fabric-1.10.5.001+1.21.11-HC-TEST3-RC16-PROVOCATION-ICON.jar`

SHA-256 :
`e81904a579b4bb3012967ebb75fc173877665f535bfcb71d6e3b4231e37cc2ed`

Correction :
- ajout de `assets/capitale/textures/spell/provocation.png` au chemin exact demandé par le log client ;
- icône candidate basée sur `rpg_series:textures/spell/ground_slam.png` ;
- `HudRenderHelper.class` est byte-for-byte identique à RC15 ;
- aucun JSON de spell, cooldown, cast, impact, dégât ou stat d'arme modifié.

## Historique

Les sources `PatchClasses.java` de la première passe 0.1.2 / RC15 restent conservées pour retracer le correctif précédent. La nouvelle implémentation Armor est dans :
- `armor-0.1.3/RuntimeArmorScaler.java`
- `armor-0.1.3/ItemStackArmorAttributeMixin.java`

## Promotion

Ne pas promouvoir ces builds dans `artifacts/jars` avant validation réelle.

Test attendu :
1. supprimer 0.1.2 et RC15 ;
2. installer uniquement Armor Balance 0.1.3 + Spell Engine RC16 des deux côtés ;
3. Justicar complet attendu : 6 + 16 + 12 + 6 = 40 ;
4. diamant vanilla attendu : 40 ;
5. vérifier dégâts/vitesse des armes inchangés ;
6. vérifier Provocation avec une vraie icône ;
7. vérifier que tous les autres spells restent visibles.
