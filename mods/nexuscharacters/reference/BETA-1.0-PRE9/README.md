# NexusCharacters — BETA 1.0 PRE9

Base exacte : **BETA 1.0 PRE8** `1.0.0-beta.8+hc.1.21.11`.

## Objet

PRE9 corrige le crash client immédiat lors de l'ouverture de **Créer un personnage**.

Le log PRE8 montre :

`java.lang.NoSuchMethodError: boolean net.tompsen.nexuscharacters.UnifiedPilositySupport.allowsPilosity(Object)`

`RacialAppearance69Support.allowsFacialHair(...)` appelle encore cette méthode. L'audit ABI confirme qu'elle existait dans PRE5, mais qu'elle a disparu lorsque `UnifiedPilositySupport.class` a été recompilé pour PRE7. PRE8 a ensuite conservé cette classe PRE7 telle quelle.

## Correction

PRE9 réinjecte **uniquement** la méthode publique manquante :

`public static boolean allowsPilosity(Object race)`

avec la sémantique exacte de PRE5 :

- `HUMAN` → true
- `NORDIC` → true
- `DWARF` → true
- autres races → false

Le correctif n'annule pas les changements PRE7 :
- les Nains gardent obligatoirement une barbe ;
- la barbe ajourée et les autres corrections cosmétiques restent présentes ;
- le layout PRE6/PRE7 reste inchangé.

Le correctif PRE8 de silence console est également préservé intégralement.

## Portée

Par rapport à PRE8, seules les entrées suivantes doivent changer :

- `net/tompsen/nexuscharacters/UnifiedPilositySupport.class`
- `fabric.mod.json`

Aucun code de sauvegarde, aucun bridge Puffish/Profile, aucun asset et aucun autre composant UI n'est recompilé.
