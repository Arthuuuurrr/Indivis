# Audit ciblé — capitale_skills_items 1.7.18 avant optimisation 1.7.19

Issue : #123

## Base vérifiée

- JAR runtime : `capitale_skills_items 1.7.18-event-access-fix`
- SHA-256 de la base : `0dfbfe3f324d40851de01346b2bced6ecb6ab2ab38a36c1bc977f5623f94b30b`
- Datapack versionné : `capitale_skills 0.10.19-RC2F TREE HARD RESTORE`

## Conclusion sur les mécaniques historiques

Le nouveau système de skills n'autorise PAS à supprimer aveuglément les anciens hooks du JAR.

Éléments vérifiés comme encore actifs ou encore requis :
- `CapSkillsDualWieldPassive173` : maintient le tag universel `capskills.dual_wield.active` et les états passifs de double arme ;
- `CapSkillsTourbillonTagAccess152` : utilisé par Tourbillon, double arme et Lance longue ;
- `CapSkillsRuntime127` : encore appelé par de nombreux hooks et utilisé depuis 1.7.17 comme pont vers l'exécution des fonctions datapack ;
- les fonctions de bridge du datapack actuel existent encore pour Tourbillon, arc, arbalète, Shadowstep, Rempart, Chaman et revolver ;
- les notes RC2/RC2B/RC2F indiquent explicitement que les sorts intrinsèques et les autorisations nécessaires sont conservés.

Les audits historiques indiquent également que plusieurs anciens chemins ont déjà été retirés de la voie active avant 1.7.18 : touche V, `/trigger`, faux paquets d'interaction, anciens mixins serveur Better Combat et ancien mixin d'interaction Tourbillon.

## Classes probablement dormantes dans le JAR 1.7.18

Les classes suivantes :
- ne figurent pas dans les entrypoints de `fabric.mod.json` ;
- ne figurent pas dans la configuration mixin active ;
- ne sont pas référencées directement par une autre classe du JAR ;
- leur nom n'est pas retrouvé comme cible de chargement réfléchi dans les autres classes inspectées.

Candidats :
- `CapSkillsAdvancedHooks`
- `CapSkillsBetaHooks`
- `CapSkillsBetterCombatCompatHooks`
- `CapSkillsBetterCombatReadOnlyBridge`
- `CapSkillsBetterCombatSpinAnimationHooks`
- `CapSkillsBowChannelHooksClean`
- `CapSkillsCivilHooks`
- `CapSkillsCombatHooks`
- `CapSkillsPacketHitDebugInitializer`
- `CapSkillsServerOnlyEntrypoint`
- `CapSkillsShieldDurabilityHooks`
- `CapSkillsTourbillonInputHooks`

### Décision pour 1.7.19

**Aucune de ces classes n'est supprimée dans 1.7.19.**

Raison : ce correctif doit rester strictement mesurable et réversible. Retirer des classes historiques en même temps que l'optimisation rendrait impossible d'attribuer une éventuelle régression et pourrait casser un chargement externe non visible dans le seul JAR.

Un nettoyage séparé pourra être fait après :
1. validation runtime de 1.7.19 ;
2. recherche dans les autres mods/datapacks ;
3. suppression par petits lots ;
4. tests fonctionnels dédiés.

## Hotspot Spark traité

Chemin :
`CapSkillsDualWieldPassive173.tickServer -> updatePlayer -> clearRuntimeTags -> CapSkillsTourbillonTagAccess152.removeTag -> findOneArg`

La 1.7.18 rescannait les méthodes de la classe runtime pour chaque suppression de tag. Comme `clearRuntimeTags` retire plusieurs tags à chaque tick et pour chaque joueur, le coût se multiplie avec le nombre de joueurs.

La 1.7.19 met seulement en cache les résolutions de méthodes/champs et les échecs de résolution.

## Non-régression recherchée

Aucune modification de :
- tag fonctionnel ;
- nom de fonction datapack ;
- règles d'arme seule / double arme ;
- logique Sentence croisée ;
- Tourbillon ;
- Lance longue ;
- arc/arbalète ;
- revolver ;
- Rempart/Chaman/Shadowstep ;
- entrypoints ;
- mixins ;
- datapack.
