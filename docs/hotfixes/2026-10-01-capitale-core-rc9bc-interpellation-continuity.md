# capitale_core 1.5.6-RC9BC — continuité des interpellations

Date : 2026-10-01

Base : `capitale_core 1.5.6-RC9BB`.

## Constat runtime

Après le correctif RC9BB/HCD b10, l'interpellation automatique fonctionne de nouveau. Les logs de test montrent toutefois une rupture UX sur certains PNJ : une session d'interpellation `CAPTURE` se termine, puis le joueur doit interagir une seconde fois avec le même PNJ pour accéder au vrai dialogue de quête.

Audit du core :
- Géraud : enchaînement automatique déjà correct, inchangé ;
- Léovic : ses choix sont ouverts automatiquement après l'interpellation, inchangé ;
- Althéon : rupture présente ;
- Aurèle : rupture présente ;
- Roch : même structure que Aurèle, rupture présente.

## Correctif

Ajout d'un bouton de réponse `[Je vous écoute.]` à la fin de l'interpellation pour :
- Althéon : `QuestChoix=5` -> `capitaine_large/ouvrir_conversation_self` ;
- Aurèle : `QuestChoix=34` -> `aurele/reoffer_self` ;
- Roch : `QuestChoix=53` -> `roch/reoffer_self`.

Le bouton reste dans le système de `tellraw` + `/trigger QuestChoix` déjà utilisé par les dialogues de quête. Aucun clic EasyNPC supplémentaire n'est nécessaire.

## Fichiers data modifiés

- `data/capitale/function/quest/dialogue/dispatch_self.mcfunction`
- `data/capitale/function/quest/spawn_dirigeable_couronne/sequence/capitaine_near_intro_l1_self.mcfunction`
- `data/capitale/function/quest/sous_le_regard_du_coeur/sequence/aurele_near_intro_l3_self.mcfunction`
- `data/capitale/function/quest/au_seuil_des_profondeurs/sequence/roch_near_intro_l3_self.mcfunction`
- `pack.mcmeta`
- `VALIDATION_RC9BC_INTERPELLATION_CONTINUITY.md`

## Invariants

- aucun changement dans `haute_capitale_dialogue` ;
- aucun changement de caméra ;
- aucun changement EasyNPC ;
- aucun changement des choix existants d'Althéon (1-4), Aurèle (30-33/40-41) ou Roch (50-52/55-57) ;
- `CAP_FLAG` continue d'empêcher les cascades dans le dispatcher ;
- chaque passerelle exige `CAP_QSEQ=0` et l'état de quête correspondant.

## Build

- ZIP : `capitale_core_1.5.6-RC9BC_INTERPELLATION_CONTINUITY_FULL.zip`
- SHA-256 : `f860bb8d27e541d0292286629a53cf7308f09306a3dfc4bedd16c6991f137c9d`
- taille : 1 555 225 octets
- archive ZIP : testée sans erreur

## Point runtime distinct

Les logs du même essai indiquent `haute_capitale_dialogue b10` côté serveur mais `b7` côté client. RC9BC n'exige pas de nouvelle version du mod, mais ce décalage doit rester visible dans la traçabilité car les évolutions client introduites après b7 ne sont pas réellement testées par cette session.
