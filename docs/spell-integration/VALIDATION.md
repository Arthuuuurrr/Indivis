# Validation statique consolidée

Date de consolidation : 22 septembre 2026.

## CapSkills RC2F

Derniers invariants vérifiés :

- JSON valides : 549 ;
- skills Puffish : 338 ;
- définitions Puffish : 338 ;
- définition manquante : 0 ;
- endpoint de connexion cassé : 0 ;
- composantes du graphe : 321 et 17 ;
- branche métiers de 17 nœuds volontairement séparée ;
- aucun besoin de reconnecter cette branche à `doctrine_root`.

Audit complet antérieur de RC2D avant restauration de l’arbre :

- 338 skills / 338 définitions ;
- 349 connexions ;
- 0 endpoint cassé ;
- 0 doublon de paire non dirigée ;
- 265 définitions d’abilities ;
- 221 abilities visibles ;
- ordre visible 1..221 continu et unique ;
- 247 rewards, 247 uniques ;
- 0 reward cassée ;
- 0 ability visible sans reward ;
- 44 abilities techniques Arsenal ;
- 26 techniques Arsenal récompensées statiquement ;
- 18 items Arsenal dynamiques non récompensés ;
- 0 spell mappé manquant dans l’univers de sources audité ;
- 0 mismatch ACTIVE/PASSIVE.

RC2F restaure uniquement le sous-arbre Puffish depuis l’état sain RC2B afin d’éliminer le risque introduit par RC2D.

## Arsenal

- 44 associations exactes item/passif ;
- 32 passifs uniques ;
- les 18 items dynamiques ont été validés contre les choices natifs et les requirements CapSkills ;
- 36 paires native-choice → ability CapSkills → compatibilité arme : PASS ;
- dual wield identique : déduplication prévue côté Spell Engine.

## Haute Capitale RPG RC2

Harness ciblé Avatar :

- staff normal non affecté : PASS ;
- Avatar sans choix bloque Water : PASS ;
- Avatar Terra autorise Stone Spear : PASS ;
- Avatar Terra bloque Air Cutter : PASS ;
- Avatar Terra bloque une invocation sans rapport : PASS.

## Spell Engine

Invariants vérifiés au fil des RC :

- ACTIVE : déduplication canonique, dernière occurrence conservée ;
- PASSIVE : déduplication seulement pour `arsenal:*` ;
- heal AIM pur : joueur derrière mob accepté ;
- cible mob rejetée comme candidat pour un joueur ;
- caster mob/NPC : comportement upstream conservé ;
- sorts mixtes HEAL+DAMAGE inchangés ;
- raycast/blocage par mur upstream conservé ;
- migration HUD uniquement ancien défaut `BOTTOM, y=-11` ;
- valeur custom `y != -11` inchangée.

## Hazenn / Spell Power / Spell Engine

Validation de la chaîne finale :

- intégrité ZIP/JAR : PASS ;
- entrées dupliquées : 0 ;
- Air ← Lightning : PASS ;
- Earth !← Nature : PASS ;
- Arcane !← Ender : PASS ;
- global Spell Power non appliqué aux écoles physiques : PASS ;
- `CASTING_MOVESPEED` exclu de la Haste : PASS ;
- +10 % Spell Resist → facteur 0,90 sur dégâts résistables : PASS ;
- dégâts non résistables/physiques inchangés : PASS ;
- +15 % Casting Movement → ×1,15 : PASS ;
- +15 % Summon Damage mêlée → ×1,15 : PASS ;
- +15 % Summon Damage sur Spell Power de l’invocation → ×1,15 : PASS.

## Witcher TEST3 RC1

- JAR valide ;
- entrées dupliquées : 0 ;
- `footwork.png` = copie byte-identique de `fast_attack.png` ;
- 73 spell JSON dans le mod ;
- 53 abilities HC mappées ;
- 0 spell ID manquant ;
- 0 mismatch de type ;
- 0 reward cassée ;
- tous les nœuds de la voie Sorceleur atteignables.

## Azure TEST3 RC1

Patch structurel vérifié ; test runtime obligatoire sur une armure Azure enchantée ciblée par l’outline.

## Limite

Tous ces résultats sont **statiques** tant qu’un scénario n’est pas explicitement marqué runtime dans la matrice de test.


## Spell Power RC8 — ClassFormat Fix

Incident déclencheur : `ClassFormatError: Illegal local variable table length 210` dans `SpellResistance.resist(...)`.

Validation du candidat RC8 :

- SHA-256 : `4f9527b9aa6b7b34531edaed25d3d5c0e6e182a816169b41d80a207ea3589fa8` ;
- intégrité JAR : PASS ;
- manifeste Fabric byte-identique à RC7 : PASS ;
- `SpellResistance.class` réécrite sans métadonnées debug locales invalides ;
- `HcHazennResistanceBridge.apply(...)` toujours présent : PASS ;
- `HcHazennSpellResistanceMixin` absent de la classe et du mixin config : PASS ;
- ASM `CheckClassAdapter` :
  - SpellResistance : PASS ;
  - SpellSchool : PASS ;
  - HcHazennResistanceBridge : PASS ;
  - HcHazennSpellPowerBridge : PASS.

Statut : **correctif structurel validé statiquement, validation Minecraft runtime requise (#70).**


## Spell Engine RC9 — HUD + Icon Sync

Candidat : `spell_engine-fabric-1.10.5.001+1.21.11-HC-TEST3-RC9-HUD-ICON-SYNC.jar`

SHA-256 : `618823d07ce96ca637e91dca512bfab2ec74eff9a5e06faea7a7f68edd9e2acd`.

### HUD

- défaut upstream/original : `x=-170, y=-11` ;
- mauvais défaut HC précédent : `x=-170, y=-34` ;
- défaut RC9 : `x=-185, y=-11` ;
- hauteur vanilla restaurée ;
- décalage horizontal : 15 px vers la gauche ;
- migrations exactes uniquement :
  - `BOTTOM + (-170,-34) → (-185,-11)` ;
  - `BOTTOM + (-170,-11) → (-185,-11)` ;
- positions personnalisées préservées.

### Icônes

- 221 abilities RC2F possèdent un spell ;
- 216 possèdent une texture explicite réutilisable ;
- conflits spell → texture : 0 ;
- 216 mappings embarqués ;
- pour un sort mappé, la spellbar utilise exactement la texture CapSkills ;
- pour un sort non mappé, fallback upstream : `namespace:textures/spell/<path>.png`.

Harness :

- `wizards:fireball → wizards:textures/spell/fireball.png` : PASS ;
- `witcher_rpg:rend_boost_a → witcher_rpg:textures/spell/strong_attack.png` : PASS ;
- `foo:bar → foo:textures/spell/bar.png` : PASS.

ASM `CheckClassAdapter` :

- SpellRender : PASS ;
- HcSpellIconFallback : PASS ;
- HudConfig : PASS ;
- HcHudMigration : PASS.

Statut : **validation statique PASS ; validation Minecraft runtime requise (#80).**
