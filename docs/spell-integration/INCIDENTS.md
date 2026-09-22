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


## 6. Crash Spell Power RC7 pendant un tick d'entité

### Symptôme

Serveur démarré correctement, puis crash pendant le tick d'un `capitale_entities:goblin` :

`java.lang.ClassFormatError: Illegal local variable table length 210 in method SpellResistance.resist(...)`.

Le gobelin n'est pas la cause : il a simplement déclenché un chemin de dégâts/résistance qui a chargé cette méthode.

### Cause

RC7 avait ajouté l'appel au bridge Hazenn à la fin de `SpellResistance.resist(...)`.

- taille bytecode originale : 210 octets ;
- taille RC7 : 219 octets ;
- les entrées de `LocalVariableTable` provenant de la classe originale se terminaient encore à l'offset 210 ;
- dans RC7, cet offset tombe au milieu de l'instruction `dstore` ajoutée à l'offset 209.

La JVM rejette donc le format de classe lorsque la méthode est réellement vérifiée/utilisée.

### Correction RC8

- conservation du bytecode fonctionnel RC7 ;
- réécriture de `SpellResistance.class` via ASM `ClassReader -> ClassWriter` avec `SKIP_DEBUG` ;
- suppression de la `LocalVariableTable` et des métadonnées debug invalides de cette classe uniquement ;
- bridge `HcHazennResistanceBridge.apply(...)` conservé ;
- ancien mixin RC6 toujours absent ;
- manifeste Fabric byte-identique à RC7.

### Validation statique RC8

- archive ZIP/JAR : PASS ;
- `CheckClassAdapter` sur SpellResistance, SpellSchool et les deux bridges Hazenn : PASS ;
- `LocalVariableTable` finale de SpellResistance : absente ;
- appel au bridge de résistance : présent ;
- ancien mixin RC6 : absent.

Issue de suivi : #70.


## 7. Spellbar trop haute et icônes de techniques d'armes manquantes

### Symptôme HUD

Après les correctifs TEST3, la spellbar était remontée dans la zone cœurs/armure.

Cause : une ancienne migration HC avait changé le défaut upstream `y=-11` en `y=-34`.

### Correction RC9

- retour à `y=-11`, hauteur d'origine alignée sur la hotbar vanilla ;
- déplacement horizontal à `x=-185`, soit 15 px vers la gauche ;
- migration uniquement des anciens défauts exacts `(-170,-34)` et `(-170,-11)` ;
- aucun écrasement d'une position personnalisée.

### Symptôme icônes

Une technique d'arme au clic droit observée sur une hache (« Coupe profonde ») affichait le damier de texture manquante. D'autres techniques pouvaient être touchées lorsque l'ID du sort ne correspondait pas au chemin réel de l'icône.

### Correction RC9

Au lieu de patcher un sort isolé, Spell Engine utilise la texture déclarée par CapSkills RC2F :

- 221 abilities avec spell auditées ;
- 216 mappings spell → texture explicites ;
- 0 conflit ;
- 5 abilities sans texture explicite conservent le fallback upstream.

Issue de validation runtime : #80.
