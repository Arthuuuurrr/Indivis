# Audit des bonus d’armures hors RPG Series — 24/09/2026

## Périmètre

La valeur brute d'armure est figée : `capitale_armor_balance 0.1.7` + `Spell Engine RC17` constituent la base ARMOR40 validée.

Cet audit ne modifie pas ARMOR40. Il porte sur les bonus secondaires, passifs de set et capacités embarquées des mods non nativement construits autour de RPG Series.

## Règle d'intégration

Un bonus intrinsèque à une armure (résistance, vitesse, passif de set, immunité, mobilité) peut rester autonome.

En revanche, un équipement qui accorde directement un sort, une capacité activable ou un conteneur de sorts doit être vérifié contre CapSkills afin qu'un reset/déverrouillage ne puisse pas être contourné.

## Matrice — première passe

| Mod | Type de bonus identifié | Spell Engine / Spell Power | Risque | Action |
| --- | --- | --- | --- | --- |
| `hazennstuff 1.0.0-b4+hc.spellcompat1` | attributs custom + effets de set | bridge HC déjà présent | élevé | audit bytecode exact en cours, anomalies confirmées |
| `landsoficaria 1.0.1` | armure vanilla-like + marche neige poudreuse | sans objet | faible | aucune adaptation spell nécessaire |
| `treasures_of_the_dead` | ArmorItem + rendu Gecko | sans objet | faible | aucune capacité combat d'armure trouvée dans les sources auditées |
| `myths_of_the_sea 1.3.0` | vitesse aquatique + passif Bake Kujira | indépendant | moyen/élevé | conserver Abaia; revoir immunité de ciblage des morts-vivants |
| `dungeonnowloading 2.2+1.21.11-mmo.b10` | bonus complet Spawner: invocation périodique | indépendant | moyen | audit du port exact requis avant modification |
| `block_factorys_bosses 2.1.2+1.21.11-fabric.b1` | attributs custom (movement recovery, etc.) | indépendant | moyen | audit du port exact requis |
| `rpg-minibosses 1.7.0+1.21.11.b3` | ArmorSetConfig / RPG Series | natif | faible | référence, pas cible principale |
| `nightreign_armor 1.6.1+1.21.11-fabric.b2` | non confirmé sur port exact | non confirmé | inconnu | bytecode exact requis |
| `remains_cave 1.0.0+1.21.11-mmo.b3` | non confirmé sur port exact | non confirmé | inconnu | bytecode exact requis |
| `haute_capitale_metiers 0.1.0.b12` | mod privé HC | non confirmé | inconnu | retrouver source/JAR exact avant modification |
| `death_knights 1.0.0` | port 1.21.11 spécifique | à vérifier | moyen | audit séparé du port exact |

## Références d'équilibrage RPG Series

Les armures RPG Series sont utilisées comme référence et non comme cible technique.

Exemples vérifiés :
- Wizards : puissance d'école par pièce ~20 % T1, 25 % T2, 30 % T3, soit ~80/100/120 % sur set complet, plus un secondaire léger.
- Rogues/Warrior : T3 autour de +5 % dégâts par pièce, +1 toughness par pièce et secondaires spécialisés.

Ces valeurs servent d'ancre : un set tiers peut être plus spécialisé ou plus rare, mais ne doit pas cumuler sans coût plusieurs rôles complets.

## Statut

- ARMOR40 : terminé / figé.
- Compatibilité Hazenn ↔ Spell Power : confirmée.
- Bonus de sets Hazenn : anomalies fonctionnelles et de cohérence confirmées.
- Mods tiers sans binaire/source exacte : ne pas patcher à l'aveugle.
