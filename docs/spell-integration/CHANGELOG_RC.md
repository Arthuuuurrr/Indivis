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

### RC7

Ajoute :

- `CASTING_MOVESPEED` Hazenn appliqué au mouvement pendant cast ;
- `SUMMON_DAMAGE` Hazenn sur les invocations.

### RC8 HUD ALIGN — intermédiaire

- restaure la hauteur vanilla de la spellbar : `y=-11` au lieu du mauvais défaut HC `y=-34` ;
- déplace la spellbar de 15 px vers la gauche : `x=-185` ;
- migration ciblée uniquement sur les anciens défauts exacts.

### RC9 HUD-ICON-SYNC

Conserve RC7/RC8 et ajoute :

- synchronisation spell → icône avec CapSkills RC2F ;
- 216 mappings explicites ;
- 0 conflit de mapping ;
- fallback upstream conservé pour les sorts non mappés ;
- `SpellRender.iconTexture` délègue à `HcSpellIconFallback` ;
- map de référence embarquée : `assets/spell_engine/hc_spell_icon_map.json` ;
- ASM `CheckClassAdapter` : PASS sur les quatre classes HUD/icônes modifiées ;
- validation runtime requise (#80).

### RC10 CAST SAFETY — candidat actuel

Conserve intégralement RC9 et ajoute deux protections globales :

- CHANNEL : une seule séquence finie par pression physique ; lorsque le process serveur est terminé, le maintien de la même touche ne relance plus automatiquement le CHANNEL ;
- le restart CHANNEL utilise l’état `START_released`, alors que CASTING non-CHANNEL conserve le chemin upstream `STOP_released` ;
- `SpellParameters.hasteAffectedValue(base, haste)` protège désormais les valeurs Haste non finies/non positives ;
- les valeurs Haste positives sous `0.1` sont ramenées au plancher normalisé de Spell Power (HASTE default 100, min 10, max 1000) ;
- résultat de division non fini → durée neutre ;
- audit des 221 abilities visibles : 0 erreur ;
- audit de l’univers complet de 319 ressources de sorts : 0 erreur ;
- 33 CHANNEL au total, et aucun CHANNEL caché supplémentaire hors arbre ;
- validation runtime requise (#83).

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

### RC7 Direct Resist — rejetée au runtime

- retire le mixin RC6 ;
- appelle directement le bridge de résistance depuis `SpellResistance.resist(...)` ;
- **rejetée** après crash runtime : `ClassFormatError: Illegal local variable table length 210`.

Cause : la méthode a été étendue de 210 à 219 octets sans réécriture correcte de la `LocalVariableTable`.

### RC8 ClassFormat Fix — candidat actuel

- conserve la logique RC7 et le bridge Hazenn ;
- réécrit uniquement `SpellResistance.class` via ASM avec `SKIP_DEBUG` ;
- supprime les tables debug invalides de cette classe ;
- aucun retour du mixin RC6 ;
- `CheckClassAdapter` : PASS ;
- validation Minecraft runtime encore requise.

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
