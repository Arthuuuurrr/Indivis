# Capitale Creatures 1.2.39 — respiration et orientation marine

Base : `capitale_creatures_bundle 1.2.38`.
Datapack associé : **BETA 0.27 inchangé**.
Issue : #121.

## Correction

La couche `MarineIdle138` imposait bien une nage 3D, mais elle ne :
- rechargeait pas l'air des entités aquatiques ;
- synchronisait pas l'orientation du modèle avec la vélocité qu'elle imposait.

Cela expliquait deux symptômes :
- des créatures marines pouvaient se noyer ;
- elles pouvaient se déplacer/attaquer en diagonale ou à reculons alors que leur yaw restait orienté ailleurs.

## 1.2.39

`CapitaleCreaturesMarineIdle139` remplace l'entrypoint 138 pour :
- Aegirocassis hostiles ;
- Dunkleosteus hostiles ;
- Mosasaurus hostiles ;
- Corpsefish ;
- Gluttonfish.

En immersion :
- `air = maxAir` à chaque tick ;
- la nage/profondeur de 1.2.38 est conservée ;
- le yaw est calculé depuis la vélocité horizontale finale ;
- yaw, bodyYaw et headYaw sont synchronisés ;
- le pitch suit la composante verticale, limité à ±35° ;
- les rotations sont interpolées avec des vitesses différentes selon l'espèce et selon idle/combat.

Les requins ne sont volontairement pas inclus : leur locomotion/orientation native fonctionne déjà correctement.

## Validation statique
- méthodes intermédiaires Minecraft 1.21.11 vérifiées : get/setYaw, get/setPitch, setBodyYaw, setHeadYaw, getMaxAir, setAir ;
- `BasicVerifier` ASM : PASS, 25 méthodes ;
- manifeste : `MarineIdle139` actif, `MarineIdle138` absent des entrypoints ;
- diff 1.2.38 -> 1.2.39 : `fabric.mod.json` modifié, 2 classes 138 retirées, 2 classes 139 ajoutées, 1 note META ajoutée ;
- aucun JAR imbriqué modifié ;
- archive JAR valide.

SHA-256 bundle :
`7ed676040db22b88cf4207d5a562afb84f541a37f25b978edbb54dcbf7f5f26d`

Test serveur requis avant fusion.
