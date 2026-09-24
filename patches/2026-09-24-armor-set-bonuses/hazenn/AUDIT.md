# HazennStuff HC-SPELLCOMPAT1 — audit des bonus d’armure

Cible exacte auditée : `hazennstuff 1.0.0-b4+hc.spellcompat1`.

## Extraction

Analyse du bytecode réel :
- 334 pièces d'armure enregistrées ;
- 94 clés/groupes de set ;
- 62 profils de modificateurs distincts.

Le port HC est beaucoup plus simple que l'amont Iron's Spells : `HnSGeoArmorItem` porte une clé de set, un éventuel effet de set et les attributs d'item.

## Compatibilité Spell Engine / Spell Power

Le bridge HC existant couvre déjà :
- `SPELL_POWER` global pour les écoles magiques ;
- puissances d'école Hazenn ;
- `CAST_TIME_REDUCTION` / `COOLDOWN_REDUCTION` vers Haste sans double-dip ;
- critique magique ;
- `SPELL_RESIST` ;
- `CASTING_MOVESPEED` ;
- `SUMMON_DAMAGE` pour les invocations.

`MANA_STEAL` reste volontairement non mappé : Haute Capitale n'a pas de ressource mana équivalente. Ne pas le convertir artificiellement.

Conclusion : la question principale n'est plus la compatibilité générale avec les sorts, mais la cohérence et l'équilibrage des profils.

## Bug fonctionnel — détection de set complet

`HnSGeoArmorItem.isWearingFullSet()` exige systématiquement quatre pièces portant exactement la même clé de set :
HEAD + CHEST + LEGS + FEET.

Cela rend impossible le déclenchement d'un bonus déclaré sur certains ensembles incomplets ou divisés en plusieurs classes.

Cas confirmés :
- `BishopOfDeceitArmorItem` : HEAD + CHEST seulement, `SUMMONER_SET_BONUS` ;
- `NecromancerArmorItem` : HEAD + CHEST + LEGS, `SUMMONER_SET_BONUS` ;
- `NamelessOneArmorItem` : HEAD + CHEST + LEGS, `SUMMONER_SET_BONUS`.

D'autres familles fragmentées (notamment Chlorophyte et Fireblossom legacy) utilisent plusieurs clés/classes et doivent être traitées explicitement avant de changer la détection.

=> Correctif nécessaire, mais pas un simple seuil "2/3 pièces" global : il faut une définition explicite des slots attendus par famille afin d'éviter qu'un set de 4 pièces s'active incomplet.

## Effets de set HC actuels

- `MAGE_SET_BONUS` : +15 % casting movement, +10 % spell resistance.
- `SWORDMASTER_SET_BONUS` : +15 % attack speed.
- `ARCHER_SET_BONUS` : +15 % movement speed, +10 % arrow damage.
- `SUMMONER_SET_BONUS` : +15 % summon damage, +10 % mana steal (mana steal actuellement inactif côté HC).
- `FIREBLOSSOM_RULER_EFFECT` : +15 % casting movement, +10 % spell resistance.
- `FIREBLOSSOM_WARRIOR_EFFECT` : +200 % ARMOR et +200 % ARMOR_TOUGHNESS en ADD_MULTIPLIED_BASE.
- `TYRANTS_GRACE_EFFECT` : +1 attack damage, +1 attack speed, +20 % casting movement, +12 % fire spell power, +100 % arrow damage.

## Anomalies confirmées

### FIREBLOSSOM_WARRIOR_EFFECT

Le bonus ARMOR/TOUGHNESS utilise `ADD_MULTIPLIED_BASE`. La base intrinsèque du joueur est 0 ; l'armure des pièces est ajoutée séparément. Ce multiplicateur peut donc n'apporter pratiquement aucune protection.

Il ne faut pas le remplacer par un multiplicateur du total, car ARMOR40 est maintenant une base validée. La correction devra être un petit `ADD_VALUE` explicite.

### TYRANTS_GRACE_EFFECT utilisé comme bonus générique

Le même bonus feu + archer + mêlée est attribué à neuf familles très différentes :
- Cryogenic Ruler ;
- Dead King ;
- Garments of the First Flamebearer ;
- Legionnaire Commander ;
- Legionnaire Ruler ;
- Legionnaire ;
- Pyrium ;
- Pyrium Battlemage ;
- Soul Legionnaire Ruler.

C'est incohérent : Cryogenic Ruler reçoit par exemple un bonus FIRE, Dead King également, et tous reçoivent +100 % ARROW_DAMAGE.

=> À remplacer par des profils spécifiques à l'archétype, pas par un unique bonus omni-classe.

### Outliers numériques

Comparés aux références RPG Series :
- `DreadsteelKnightArmorItem` (4 pièces) : +6 attack damage ADD_VALUE, +10 max health et 4× +15 % attack speed ADD_MULTIPLIED_TOTAL, puis +15 % attack speed via le bonus Swordmaster. Candidat clair à réduction.
- `PermafrostPrinceArmorItem` : chaque pièce apporte +25 % movement speed ADD_MULTIPLIED_TOTAL ; quatre pièces cumulent 1.0 de modificateurs avant le bonus Archer +15 %. Candidat clair à réduction.
- `FrostbiteHunterArmorItem` : quatre fois +15 % movement speed ADD_MULTIPLIED_TOTAL + bonus Archer. À réduire.
- `FrostbiteKnightArmorItem` : +4 attack damage au total, quatre fois +10 % attack speed total, plus spell power et bonus Swordmaster. Hybride trop dense à comparer aux paliers finaux.

### Variantes visuelles avec gameplay différent

Des variantes censées représenter la même famille ne partagent pas toujours le même profil :
- Blazeborne vs Geckolib Blazeborne : la variante Gecko ajoute notamment Eldritch spell power ;
- Dark Ritual Templar vs variante Gecko : valeurs/opérations différentes.

Ces différences doivent être normalisées sauf intention de gameplay documentée.

## Ce qui n'est PAS un problème en soi

Les sets magiques standard à +15 % école +15 % spell power par pièce atteignent environ +120 % sur leur école en set complet. C'est du même ordre que Wizards T3 (+30 % école par pièce = +120 % complet).

Il ne faut donc pas appliquer une baisse globale à toutes les armures Hazenn.

## Correctifs à produire

1. Définition explicite des pièces requises pour les sets partiels.
2. Remplacement de `FIREBLOSSOM_WARRIOR_EFFECT` par un bonus fixe compatible ARMOR40.
3. Scission de `TYRANTS_GRACE_EFFECT` en bonus spécialisés.
4. Réduction ciblée des outliers Dreadsteel / Permafrost / Frostbite, sans nerf global.
5. Normalisation des variantes visuelles qui divergent accidentellement.
6. Laisser les stats mana inactives tant qu'aucune ressource HC correspondante n'existe, plutôt que de les remapper artificiellement.
