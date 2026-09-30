# capitale_skills_items 1.7.19 — Reflection Cache

Issue : #123  
Branche : `fix/capitale-skills-items-reflection-cache`

## Nature de cette source

Le dépôt ne contient pas encore l'arbre source canonique complet de `capitale_skills_items 1.7.18` (#72).

Ce dossier archive donc **le patch source exact de la classe remplacée**, avec l'audit et les validations nécessaires pour reproduire la modification à partir du JAR 1.7.18.

Il ne faut pas présenter ce dossier comme le source complet historique du mod.

## Base

JAR :
`capitale_skills_items_fabric_1_7_18_EVENT_ACCESS_FIX_1_21_11.jar`

SHA-256 :
`0dfbfe3f324d40851de01346b2bced6ecb6ab2ab38a36c1bc977f5623f94b30b`

## Build candidat

JAR :
`capitale_skills_items_fabric_1_7_19_REFLECTION_CACHE_1_21_11.jar`

SHA-256 :
`ca5e296c200c986c9f2d577dacf458e5c1160466edc23fa1bbd3e74ebf09ae4b`

Version Fabric :
`1.7.19-reflection-cache`

## Entrées différentes par rapport à 1.7.18

Modifiées :
- `fabric.mod.json`
- `fr/hautecapitale/skillsitems/CapSkillsTourbillonTagAccess152.class`

Ajoutée :
- `PATCH_NOTES_1_7_19_REFLECTION_CACHE.md`

Aucune autre classe mécanique n'est remplacée.

## Correctif

`CapSkillsTourbillonTagAccess152` conserve les mêmes noms de mappings et le même ordre de fallback que 1.7.18, mais mémorise :
- méthodes sans argument ;
- méthodes à un argument ;
- champs ;
- échecs de résolution.

Cela supprime les rescans `Class.getDeclaredMethods/getMethods` répétés à chaque tick dans le chemin double arme.

## Validation

- compilation Java 21 : PASS ;
- chargement de la classe : PASS ;
- mappings nommés : PASS ;
- mappings intermédiaires : PASS ;
- fallback champ : PASS ;
- membre privé hérité : PASS ;
- valeurs de retour de suppression : PASS ;
- JAR/ZIP : intègre ;
- signatures JAR : aucune ;
- comparaison des entrées 1.7.18 -> 1.7.19 : conforme à la liste ci-dessus.

Benchmark synthétique local, classe de 1200 méthodes / 100 000 suppressions :
- 1.7.18 : environ 2,27 s ;
- 1.7.19 : environ 0,04–0,05 s.

Ce benchmark démontre uniquement la suppression du rescannage réfléchi ; il ne remplace pas un Spark Minecraft.

## Runtime encore requis

Avant merge/déploiement définitif :
- démarrage client/serveur ;
- Tourbillon arme seule ;
- refus Tourbillon double arme ;
- Sentence croisée ;
- changement rapide des mains ;
- reset CapSkills + reconnexion ;
- logs sans erreur de tags ;
- Spark comparable au profil de référence.

## Nettoyage d'ancien code

Voir `AUDIT_LEGACY_HOOKS.md`.

Plusieurs classes paraissent dormantes dans 1.7.18, mais **aucune n'est supprimée dans cette version** afin de ne pas mélanger optimisation et nettoyage fonctionnel.
