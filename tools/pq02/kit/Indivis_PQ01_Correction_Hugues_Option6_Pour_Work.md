# INDIVIS — ADDENDUM WORK : correction unique de PQ01 (Hugues, choix 6)

**Contexte :** mission Work de refonte PQ02 selon le script consolidé v1.0. Une seule exception de périmètre est autorisée pour PQ01. Aucun rework complet de PQ01 n'est demandé.

## Ce que l'utilisateur demande

Retirer du dialogue de Hugues **l'option de menu n° 6** :

> Écouter leur échange sur les procédures.

**Raison :** cette option est proposée immédiatement dans une conversation dont Hugues a déjà prononcé la phrase d'accroche ; la transition est artificielle. Il faut supprimer ce choix, **pas** le réécrire, ni le déplacer ailleurs. L'accroche de Hugues elle-même doit être conservée.

## Repérage vérifié dans l'ancien `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF7.zip`

La phrase apparaît dans six fonctions de menus, avec la commande `/trigger QuestChoix set $(b6)` :

- `data/capitale/function/pq01/menu/hugues_menu.mcfunction`
- `data/capitale/function/pq01/menu/hugues_36.mcfunction`
- `data/capitale/function/pq01/menu/hugues_38.mcfunction`
- `data/capitale/function/pq01/menu/hugues_40.mcfunction`
- `data/capitale/function/pq01/menu/hugues_42.mcfunction`
- `data/capitale/function/pq01/menu/hugues_44.mcfunction`

La sélection 6 renvoie dans les six fonctions homologues `pq01/choice/hugues_*` vers :

```mcfunction
execute if score @s hcm_pq1_pick matches 6 run return run function capitale:pq01/banter_self
```

Le menu de Hugues a en HF7 comme accroche prononcée :

> Trois fois qu'on me fait recompter les mêmes caisses... Ah, pardon. Vous cherchiez quelque chose ?

**Cette accroche est à conserver ; il faut seulement éliminer l'option jaune « Écouter leur échange sur les procédures. ».**

## Règles du correctif

1. Identifier la disposition réelle dans **le core actuel** ; les noms ci-dessus proviennent de HF7, non d'une version récente certifiée.
2. Supprimer la ligne du choix n° 6 dans **tous** les menus de Hugues concernés, et neutraliser **uniquement** son chemin de sélection associé pour éviter un déclenchement résiduel.
3. Conserver les choix 1 à 5, 7 et « Fermer la conversation », avec **leurs identifiants actuels**. Ne pas décaler les numéros.
4. Ne pas supprimer le moteur de banter global, les voix, les caméras, les autres scènes, ni modifier les autres PNJ de PQ01.
5. Vérifier l'ensemble des retours aux menus après les branches Hugues, ainsi que les triggers, les logiques d'ouverture et les répliques suivantes ; vérifier qu'un ancien choix 6 ne peut pas relancer une scène incohérente.
6. Documenter dans un diff le nombre exact de fichiers édités et expliquer toute différence par rapport à HF7. Tester en jeu quand un serveur de test est disponible ; ne pas prétendre que ce test a été réalisé s'il ne l'a pas été.

**Limite du périmètre :** aucune autre correction narrative ni technique de PQ01 ne doit être introduite pendant ce chantier, sauf si indispensable à la suppression du choix et justifiée dans le rapport.
