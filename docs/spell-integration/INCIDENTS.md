# Incidents et diagnostics rencontrés

## 1. Arbre CapSkills vide

### Symptôme

Après installation de RC2D, aucun arbre n’était affiché.

### Analyse

Les fichiers Puffish structuraux `skills.json`, `connections.json` et `definitions.json` n’avaient pas été directement modifiés entre RC2C et RC2D. La piste retenue était un échec de chargement global ou une modification périphérique devenue inutile.

### Correction

RC2F restaure **tout** `data/capskills/puffish_skills/` depuis RC2B, état antérieur considéré sain.

Le verrou Avatar a été déplacé côté Haute Capitale RPG au lieu de dépendre d’un bricolage de tags dans l’arbre.

## 2. Déconnexion au /give Arsenal

### Symptôme

Un joueur créatif était déconnecté avec :

`DecoderException: Failed to decode packet 'serverbound/minecraft:set_creative_mode_slot'`.

### Cause

Arsenal était chargé côté client mais absent du serveur. Le client envoyait un ItemStack `arsenal:*` que le registre serveur ne connaissait pas.

Le même audit a montré que `capitale_skills_items` manquait aussi côté serveur, ce qui produisait des `Unknown item 'capitale_skills_items:...'` pendant le chargement de fonctions CapSkills.

### Correction

Installer les mêmes JAR Arsenal et `capitale_skills_items` côté serveur.

## 3. Crash Spell Power RC6 au démarrage

### Symptôme

Client et serveur pouvaient s’arrêter pendant la phase Mixin avec :

`HcHazennSpellResistanceMixin ... missing an @Mixin annotation`.

### Cause retenue

RC6 enregistrait un nouveau mixin de résistance dans `spell_power.mixins.json`. Ce point d’extension s’est révélé fragile au chargement réel.

### Correction RC7

- suppression de `HcHazennSpellResistanceMixin.class` ;
- suppression de son entrée dans `spell_power.mixins.json` ;
- appel direct de `HcHazennResistanceBridge.apply(...)` depuis `SpellResistance.resist(...)`.

### Piège de version client/serveur

Après correction serveur, un crash client identique a été observé : le log montrait encore **Spell Power RC6 côté client**.

Toujours vérifier la ligne de chargement exacte du mod avant d’analyser une nouvelle régression.

## 4. Hazenn : intégration initialement incomplète

### Problème

La première intégration ne modifiait que Spell Power et ne corrigeait pas toutes les statistiques Hazenn réellement utilisées par les sets.

### Correction

Hazennstuff HC-SPELLCOMPAT1 corrige la base neutre de plusieurs attributs multiplicatifs. Spell Power/Spell Engine prennent ensuite en charge :

- puissance ;
- Haste ;
- critique ;
- résistance magique ;
- vitesse de déplacement pendant cast ;
- dégâts des invocations.

`MANA_STEAL` reste explicitement non mappé.

## 5. Leçon de validation

Les trois incidents ci-dessus montrent pourquoi :

- un JAR structurellement valide peut encore échouer au runtime ;
- la liste exacte des mods chargés client/serveur doit être vérifiée avant toute correction ;
- une RC remplacée doit être retirée des deux côtés ;
- les tests d’intégration doivent être conservés dans le dépôt.
