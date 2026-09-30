# Audit du travail de Clément — état vérifié au 30/09/2026

Remplace la version du 24/09, dont plusieurs constats étaient faux ou dépassés (voir « Corrections de
l'audit du 24/09 » en fin de document).

## Méthode et limites

Ce qui a été **réellement fait** :

- lecture de `main`, des PR ouvertes, de toutes les branches distantes et de `server-manifest.yml` (sur `main` et
  dans chaque PR) ;
- mesure des builds sur le poste de développement (`C:\Users\denne\<projet>`) et dans le dossier `mods` du client
  de Clément ;
- **recompilation** des 6 modules de #104 depuis la branche de la PR, puis comparaison **fichier par fichier**
  avec les JAR réellement livrés (manifeste exclu, fins de ligne CRLF/LF neutralisées) ;
- comparaison des JAR de `haute-capitale-rpg` (b2 poste ↔ RC2 ↔ RC3 ↔ b4) ;
- lecture du code du datapack `hc_quetes` pour #108, #109, #111, #115 ;
- chargement réel des zips `hc_quetes` b17 et b18 sur un serveur dédié 1.21.11 de test.

**Limites** : pas d'accès au serveur Indivis (distant). Les versions « serveur » viennent du manifeste et de sa date.
Aucun test en jeu réel n'a été fait pendant cet audit.

## Modules de Clément

SHA-256 : 16 premiers caractères. « Correspondance » = une recompilation depuis Git redonne le contenu du JAR livré.

| Module | Serveur (source) | Client de Clément (30/09) | Dans Git | Dernière source (poste) | JAR livré | Correspondance source → JAR | Suivi |
|---|---|---|---|---|---|---|---|
| haute-capitale-rpg | b2 **RC2** (manifeste) | RC3 FIREARMS-SPELLBAR | RC2 dans `mods/` ; b4 dans #104 ; patch RC3 dans #92 | b4 | RC2 `65824c4c…`, RC3 `08f03d4d…`, b4 `54fdeb16…` | b4 : **confirmée** ; RC2 : **aucune source** | #105 |
| haute-capitale-dialogue | b7 | b8 | b7 dans #104 | b8 | b7 `959ebe1d…`, b8 `428c771e…` | b7 : **confirmée** (après `6abc026`) | #104 |
| haute-capitale-fusils | b3 | b3 | b3 dans `mods/` + sources #104 ; patch b4 dans #92 | b3 | `0979a7c9…` | **confirmée** | #3, #4 |
| haute-capitale-spawns | non listé | b6 | #104 | b6 | `7dbf1fa4…` | **confirmée** | #72 |
| haute-capitale-party | non listé | b9 | #104 | b9 + 1 modif non publiée | `b19a4268…` | **non** : touche P → ² modifiée après le build b9 | #104 |
| haute-capitale-metiers | non listé | b12 | #104 | b12 | `8db5970b…` | **confirmée** (tests 76/76) | #72 |
| journal `haute_capitale_quetes` | b3 (manifeste, nom corrigé) | b5 | absent | b5 | b5 `bce1c3c5…` | non vérifiée | #30, #72 |
| datapack `hc_quetes` | b8 | — | absent | b18 (30/09) | b17 = dernier zip correct ; **b18 illisible** | non vérifiée | #33, #108, #109, #111, #115 |
| haute-capitale-pirates | b4 | b4 | absent | b4 | `567f3130…` | non vérifiée | #72 |
| dungeonz | non déployé | b23 | absent | b23 (Git local) | `a8118510…` | non vérifiée | #32, #72 |
| capitale-prison-glace | non listé | — | absent | b6 (Git local) | `0a0ad2cd…` | non vérifiée | #72 |
| hc-necromancer | non listé | b3 | absent | b3 | `462d8010…` | non vérifiée | #72 |
| haute-capitale-orcs | non listé | b9 | absent | b9 | `1489c804…` | non vérifiée | #72 |
| dungeonnowloading-mmo | non listé | b10 | absent | b10 | `9fec82b3…` | non vérifiée | #72 |
| capitale_creatures_bundle (base) | 1.2.16 (`main`) / 1.2.17 (#100) | 1.2.16 | patchs 1.2.27+ (Arthur) | base 1.2.16 = projet `autonomous-orc-mobs` | 1.2.16 `0c3bddde…` | non vérifiée | #61, #100 |

Ajouts depuis le 26/09, chacun avec son issue « à tester » (sources à importer) : `hurans` b81 #140,
`goblins_tyranny` b4 #141, `tral` b8 #142, `legendaryshrines` b2 #143, `ancient_remnants` b1 #144, `hmag` b1 #145,
`thalassophobia` b1 #146, `mythicmounts` ground b3 + montures du journal b5 #30.

## Constats principaux

1. **`haute-capitale-rpg` — deux lignées concurrentes.** La production RC1 → RC2 a été obtenue par patchs binaires
   sur b2 (main/off-hand, ordre explicite, abilities Arsenal ; `HcAvatarStaffGate`, `AbilityFeature`/`AbilityResolver`),
   sans source. b3/b4 partent de b2 sans ces patchs. **Déployer b4 effacerait RC1–RC3.** Provenance des patchs
   RC1/RC2 à établir avec Arthur avant toute fusion des lignées. (#105)
2. **Import #104 — un fichier avait été exclu en silence** par la règle `debug/` du `.gitignore` racine
   (`DialogueDebugOverlay.java`, qui fait partie de b7) : les sources dialogue ne compilaient pas. Corrigé par
   `6abc026` ; vérifié par recompilation.
3. **party** : les sources contiennent une modification postérieure à b9 et jamais publiée (touche par défaut du
   menu de groupe P → ²). Décision en attente : produire un b10 ou revenir à b9.
4. **metiers** : pas de wrapper Gradle, se compile avec Gradle 9.5.1 installé.
5. **`hc_quetes` b18 est illisible** : ses entrées de zip utilisent `\` ; Minecraft l'accepte mais n'en charge aucun
   fichier (prouvé : `function hc_quetes:lib/anti_double` → « Unknown function » avec b18 seul, « returned 1 » avec
   b17). Ne pas le déployer ; b17 est le dernier zip correct.
6. **Quêtes** : #115 corrigé en b10 (verrou `lib/anti_double` dans les 21 points d'entrée), à tester sur le serveur ;
   #108 sans défaut de code (marqueur cliquable probablement non posé) ; **#109 et #111 : défauts confirmés, non
   corrigés** — la mort de la sorcière / de la Rongeuse n'est comptée qu'à un état précis puis le compteur est remis
   à 0 à chaque tick. Même mécanisme utilisé par 6 autres ennemis de quête : non audité.
7. **`haute_capitale_spawns`** ne figure pas dans la liste « deployed » du manifeste alors que plusieurs objectifs de
   quête dépendent de ses compteurs : présence sur le serveur à confirmer.

## Actions faites (30/09)

- `6abc026` (PR #104) : restauration de `DialogueDebugOverlay.java`.
- `b1e5314` : `server-manifest.yml` — `haute_capitale_quests` → `haute_capitale_quetes`.
- `docs/MODULES.md` : lignes des modules de Clément mises à jour.
- Issues : #105 corrigée (ne pas déployer b4, -P2) ; #140–#146 créées ; #30 complétée ; #72 complétée ;
  commentaires de vérification sur #108, #109, #111, #115.

## Reste à faire

- Décision party (b10 ou b9) et revue de #104.
- Import des sources manquantes, depuis la version la plus récente au moment de l'import (#72).
- Provenance des patchs RC1/RC2 de `haute-capitale-rpg` (#105).
- Corrections de #109 et #111 (et audit des 6 autres ennemis comptés de la même façon).
- Refaire le zip `hc_quetes` b18 avec des séparateurs `/`.

## Corrections de l'audit du 24/09

| Affirmation du 24/09 | Réalité vérifiée |
|---|---|
| « déployer ou archiver `haute-capitale-rpg` b4 » | dangereux : b4 ne contient pas RC1–RC3 (constat 1) |
| « `hc_quetes` b10 sur le poste » | b18 au 30/09 (b17 = dernier zip correct) |
| bundle créatures 1.2.16 comme référence | 1.2.17 en production selon #100, patchs jusqu'à 1.2.32 sur `main` |
| « Haute Capitale Quests » | l'id réel est `haute_capitale_quetes` |
| dialogue b7 = dernier état | b8 construit (baguette Easy NPC) |
