# État final TEST3 — 22 septembre 2026

## Versions de référence

| Composant | Version de référence | SHA-256 |
|---|---|---|
| Haute Capitale RPG | `0.3.0+1.21.11.b2.HC.TEST3.RC2` | `65824c4c54c96825a3ffdc2e8c441c690d9f4a466622efd62b33ff8c6d7548e4` |
| Spell Engine | `1.10.5.001+1.21.11-HC-TEST3-RC9-HUD-ICON-SYNC` *(candidat runtime)* | `618823d07ce96ca637e91dca512bfab2ec74eff9a5e06faea7a7f68edd9e2acd` |
| Spell Power | `1.6.1.001+1.21.11-HC-HAZENN-PRECISE-TEST3-RC8-CLASSFORMAT-FIX` *(candidat runtime)* | `4f9527b9aa6b7b34531edaed25d3d5c0e6e182a816169b41d80a207ea3589fa8` |
| Hazennstuff | `1.0.0-b4+hc.spellcompat1` | `88beb1a43a5d3f4a2ee5f4391937cf2dc0a67e6d8a587f2cbc6203e85846a748` |
| AzureLibArmor | `3.1.4-HC-TEST3-RC1` | `12a93c7fba4a58cff6f9608c537cfb0acde996123a079ceceb23a23fa03ef1b8` |
| Witcher RPG | `3.1.0-HC-FOOTWORK-TEST3-RC1` | `29ab8c9ba7835abcfdd6c9e384965c275a4f23f1e8c8059840e0d27b3b90e9de` |
| Arsenal | `1.5.1.002-HC-FR-PRIMARY-ORDER1` | `d335876b27273cc952a149a3b0271523f9126101953925a7c35d2fdde29a53d6` |
| capitale_skills_items | `1.7.18-event-access-fix` | `0dfbfe3f324d40851de01346b2bced6ecb6ab2ab38a36c1bc977f5623f94b30b` |
| CapSkills datapack | `0.10.19 RC2F TREE HARD RESTORE` | `109ec0428909c009d6599637cc0b23a90c78bd84c9d5772c6447ecf01a3d34f4` |

## Décisions fonctionnelles finales

### Haute Capitale RPG

- résolution d’ability sur main hand / off hand / any hand ;
- suivi des changements des deux mains ;
- abilities techniques Arsenal non exposées dans la barre de sorts ;
- ordre explicite des abilities ;
- verrou ciblé du **Bâton de l’Avatar** :
  - Water Whip ;
  - Stone Spear ;
  - Air Cutter ;
- aucun verrou générique `SpellChoice` appliqué aux autres armes.

### Spell Engine

- déduplication des ACTIVE en conservant la dernière occurrence ;
- déduplication des PASSIVE uniquement pour `arsenal:*` ;
- gate Arsenal exacte item/passif/main-off hand ;
- filtres heal : un joueur peut cibler un joueur derrière un mob/boss pour les heals AIM purs ;
- Holy Shock / Holy Beam et autres sorts mixtes non modifiés ;
- comportement des casters mob/NPC conservé ;
- HUD RC9 : hauteur vanilla restaurée (`y=-11`) et décalage gauche à `x=-185`; seuls les anciens défauts exacts `(-170,-34)` et `(-170,-11)` sont migrés ;
- spellbar RC9 : 216 sorts mappés utilisent exactement l’icône déclarée par leur ability CapSkills RC2F ; les sorts non mappés conservent le fallback upstream ;
- vitesse d’incantation Hazenn séparée de la Haste ;
- dégâts d’invocation Hazenn pris en charge pour les entités invoquées.

### Arsenal

- 44 associations item/passif exactes ;
- 32 passifs uniques ;
- les passifs restent natifs aux équipements Arsenal ;
- aucun passif Arsenal injecté dans le container HC ;
- 18 items dynamiques vérifient le sort choisi/disponible ;
- 26 items statiques passent par des abilities techniques cachées ;
- dual wield : même passif dédupliqué par événement.

### CapSkills

État de l’arbre RC2F :

- 338 skills ;
- 338 définitions ;
- 0 définition manquante ;
- 0 endpoint de connexion cassé ;
- graphe attendu en 2 composantes : 321 + 17 ;
- la composante 17 correspond volontairement à la branche métiers/crafting séparée ;
- 549 JSON valides au dernier audit ;
- l’arbre Puffish de RC2F est restauré depuis l’état sain RC2B.

Le Bâton de l’Avatar n’est plus contrôlé par une altération risquée de l’arbre : le gate est assuré côté Haute Capitale RPG.

### Hazennstuff / Spell Power

Mappings précis seulement :

- Fire → Fire ;
- Ice/Frost → Frost ;
- Lightning → Lightning et Air ;
- Hydro → Water ;
- Nature → Nature ;
- Holy/Radiance → Healing ;
- Blood → Blood ;
- Shadow → Unholy/Shadow selon l’école résolue.

Mappings volontairement rejetés :

- Nature → Earth ;
- Ender/Cosmic/Eldritch/Ritual/Technomancy → Arcane générique.

Compatibilités finales :

- puissance magique ;
- cast time / cooldown → Haste sans double comptage ;
- Casting Movement Speed → déplacement pendant cast, pas Haste ;
- critique magique ;
- Spell Resist uniquement sur dégâts spell résistables ;
- Summon Damage uniquement sur les invocations Spell Engine.

`MANA_STEAL` reste non mappé faute de ressource mana équivalente dans Haute Capitale.

### Witcher RPG

- texture `footwork.png` restaurée ;
- 53 abilities HC valides ;
- progression corrigée :
  - Fondamentaux → Escrime → Maîtrise mêlée ;
  - Signes fondamentaux → Sign de base → Maîtrise des Signes ;
- bonus intrinsèques d’armes/sets/glyphes/talismans laissés liés à l’équipement.

### AzureLibArmor

Le patch TEST3 RC1 conserve les correctifs antérieurs et ajoute la protection du rendu outline : le glint est désactivé uniquement pendant l’outline, puis reste normal hors outline.

## Statut

**Statique :** chaîne auditée et consolidée. Spell Power RC8 corrige le `ClassFormatError` de RC7 et reste à revalider en jeu. Spell Engine RC9 corrige le placement HUD et synchronise les icônes avec CapSkills ; validation runtime requise (#80).

**Runtime :** les scénarios de [RUNTIME_MATRIX.md](RUNTIME_MATRIX.md) restent la référence avant de considérer l’ensemble STABLE.
