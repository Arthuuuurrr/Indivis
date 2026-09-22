# Snapshot runtime — 22/09/2026

Cette page n'est pas une roadmap. Elle sert uniquement d'inventaire de ce qui est **visible comme déployé** dans les captures serveur/client/datapacks.

## Divergences client / serveur confirmées

| Module | Serveur | Client | Action |
|---|---|---|---|
| Haute Capitale Quests | b3 | b2 | aligner le client sur b3 puis tester |
| Haute Capitale Pirates | b4 | b2 | aligner le client sur b4 puis tester |
| Spell Power | RC6 CLASSFORMAT FIX | RC8 CLASSFORMAT FIX | déployer RC8 côté serveur puis valider #70 |

Le reste des modules Haute Capitale les plus sensibles visibles sur les captures est aligné : Nexus alpha1.1, HUD 1.3.2, skills-items 1.7.18, creatures-bundle 1.2.16, dialogue b7, RPG RC2, Spell Engine RC7, Hazennstuff SPELLCOMPAT1, Arsenal PRIMARY ORDER1, AzureLibArmor RC1, Witcher Footwork RC1, Heraldry 0.1.7 et MMO Music Zones 1.2.7.

## Point de nettoyage serveur

Deux versions de PlayerAnimationLibFabric sont visibles simultanément :
- 1.1.10
- 1.1.7

La 1.1.7 est une ancienne version et doit être vérifiée comme reliquat avant le prochain redémarrage.

## Datapacks visibles sur le serveur

Développement interne :
- capitale_skills/ ;
- capitale_core/ ;
- orc_camp/ ;
- Arthur DataPack 1.0/ ;
- hc_quetes-0.1.0.b8.zip ;
- quete_josue.zip ;
- capitale_abilities.zip ;
- capitale_creatures_biomes_BETA_0_10.zip.

Tiers / utilitaires visibles :
- armor_statues_v2.9.2.zip ;
- A VALIDER Arkdisplay v1.1.15 (MC 1.21-1.21.11).zip ;
- A VALIDER unlock all recipes v2.0.16 (MC 1.21-1.21.11).zip ;
- Structory_1.21_v1.3.14.zip ;
- Structory_Towers_1.21_v1.0.15.zip ;
- Trek_1.21.11_B0.6_fixed.zip.

Attention : la capture peut ne pas montrer les éléments situés plus bas dans la liste.

## Écart Git / serveur important

Le dépôt contient actuellement la source complète de `capitale_creatures_biomes BETA 0.12`, alors que le serveur montre **BETA 0.10** déployé. BETA 0.12 est donc une source/candidate plus récente, pas une preuve de version de production.
