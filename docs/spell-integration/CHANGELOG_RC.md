# Historique des RC TEST3

## Haute Capitale RPG

### TEST3 RC1

Base fonctionnelle de la lignée TEST3 :

- lecture main hand / off hand / any hand ;
- dirty tracking des deux mains ;
- abilities techniques Arsenal exclues de la barre visible ;
- ordre explicite des abilities.

### TEST3 RC2 — référence finale

Ajoute le gate ciblé du Bâton de l’Avatar :

- staff normal inchangé ;
- ability du staff Avatar autorisée seulement si le sort est effectivement présent dans son container natif choisi ;
- aucun verrou générique SpellChoice.

## Spell Engine

### RC1 → RC4

Travail successif sur :

- déduplication ACTIVE/PASSIVE ;
- Arsenal gate ;
- heal AIM pur à travers mobs ;
- migration HUD ;
- préservation du comportement caster mob/NPC.

### RC5

Référence consolidée avant extension Hazenn.

### RC7 — référence finale

Ajoute :

- `CASTING_MOVESPEED` Hazenn appliqué au mouvement pendant cast ;
- `SUMMON_DAMAGE` Hazenn sur les invocations.

## Spell Power

### RC1

Premier bridge Hazenn.

### RC2

Mapping précis :

- Air ← Lightning ;
- pas Nature → Earth ;
- pas de gros agrégat Arcane ;
- POWER uniquement.

### RC6

Extension :

- Haste ;
- crit ;
- résistance ;
- summon Spell Power.

**Rejetée en runtime** : crash au PREPARE Mixin lié à
`HcHazennSpellResistanceMixin`.

### RC7 Direct Resist — référence finale

- retire le mixin RC6 ;
- appelle directement le bridge de résistance depuis `SpellResistance.resist(...)`.

## Hazennstuff

### HC-SPELLCOMPAT1 — référence finale

Corrige les bases neutres des attributs de pourcentage :

- cooldown reduction ;
- cast time reduction ;
- casting movement speed ;
- magic projectile crit chance.

Les pourcentages définis par les items/sets ne sont pas rééquilibrés.

## CapSkills

### RC2B

- autorisation dynamique Arsenal.

### RC2C

- nettoyage diagnostics/version ;
- pas de changement gameplay majeur.

### RC2D

- tentative de correction Avatar par modification de compatibilités/tags ;
- abandonnée après arbre vide.

### RC2E

- première restauration.

### RC2F TREE HARD RESTORE — référence finale

- restaure tout `data/capskills/puffish_skills/` depuis RC2B ;
- conserve les corrections hors arbre ;
- délègue le verrou Avatar à Haute Capitale RPG RC2.

## AzureLibArmor

### TEST3 RC1 — référence finale

Conserve les correctifs antérieurs + correction outline/glint.

## Witcher RPG

### TEST3 RC1 — référence finale

Ajoute `footwork.png` et conserve la structure CapSkills corrigée.

## Arsenal

Le JAR `HC-FR-PRIMARY-ORDER1` est conservé ; les gates TEST3 sont portées
par Spell Engine + CapSkills.

## capitale_skills_items

`1.7.18-event-access-fix` reste la version de référence.
