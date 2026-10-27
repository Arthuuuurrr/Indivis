# INDIVIS — Guide d'intégration quêtes / NexusCharacters / accès / EasyNPC / Xaero

**Statut : cahier des charges de conception et de vérification, PAS correctif compilé.**  
**Portée :** MQ01–MQ03, amorce MQ04, et conventions pour les quêtes futures de Clément.  
**Bases consultées :** `capitale_core_1.5.6-RC9BC_INTERPELLATION_CONTINUITY_FULL(1).zip`, `hc_quetes-0.1.0.b21_LIVRES_LORE_RECANON_v16_1(2).zip`, `haute-capitale-quetes-0.1.0+1.21.11.b6(2).jar`.  
**Attention :** le core actuel pourrait être en 1.5.7 et NexusCharacters en PRE34 ou au-delà : identifier et comparer les versions RÉELLEMENT utilisées avant modification. Ne pas déployer sur le serveur de production et ne pas écraser les originaux.

## 1. Règles de progression et déclenchement

- Prologue terminé ; personnage au minimum **Résident** ; `CAP_POINTS_TOTAL >= 5` (seuil provisoire, sans citoyenneté obligatoire) ; MQ01 non commencée.
- `CAP_POINTS_TOTAL` signifie **points de compétences gagnés historiquement**, jamais les points disponibles après dépense. Le mettre à jour sur l'événement d'obtention effectif de CapSkills, une fois par gain, et le conserver dans l'instantané du personnage Nexus. Contrôler les gains du prologue, des quêtes et des autres sources ; aucun double comptage au chargement ou lors d'un remboursement.
- L'événement de convocation est **en attente** jusqu'à ce que le joueur se trouve dans un lieu urbain éligible et sûr ; le messager apparaît pour le personnage actif et remet une lettre personnalisée. Une fois le dialogue terminé, MQ01 devient active ; présentation aux gardes -> MQ02 ; MQ02 -> MQ03 ; MQ03 -> MQ04 et waypoint Havre-Fort.
- La citoyenneté impériale demeure facultative pour cette ligne ; elle peut conditionner **d'autres** lignes de quêtes. Les titres du journal ne doivent pas la présenter comme un prérequis de la quête principale.

## 2. Les accès du core : constats vérifiés dans RC9BC

- `ACCESS_* = 0` : pas d'autorisation.
- `ACCESS_* = 1` : autorisation accordée par `access/grant/*` ; peu fréquente mais à préserver.
- `ACCESS_* = 2` : autorisation dérivée du rang par `access/auto/*` ou `access/auto_silent/*`. **La valeur 2 n'est pas en soi chronométrée.**
- Les laissez-passer courts du core sont gérés séparément par **`CAP_PASS_ZONE` + `CAP_PASS_TIMER`** (exemples confirmés : mission d'Odon / Quartiers hauts sud, mission de la Technicienne / Cœur ; renouvellement à 12 000 ticks, annoncé comme dix minutes). Le core gère aussi le délai de contexte des gardes via `CAP_CTX_TIMER` : ce n'est pas une expiration d'`ACCESS_*`.
- L'accès Tribunal existe comme `ACCESS_TRIBUNAL` ; les chemins judiciaires et contrôles concrets doivent être réaudités dans le **pack effectivement chargé**. Ne pas substituer une autorisation de quête à une présentation judiciaire obligatoire.

### Proposition : valeur 3 = droit effectif de quête, prioritaire

- `ACCESS_* = 3` indique qu'une ou plusieurs quêtes donnent temporairement accès à un lieu sans minutage universel ; cette valeur **prend le dessus** pendant la quête, y compris sur `2`.
- Conserver **séparément** les origines des droits (rang, autorisation manuelle, sources de quête). Ne jamais perdre un droit de rang ou manuel quand une quête finit. Le nombre `3` seul n'indique pas quelle quête est responsable ni si un accès `1` était préexistant.
- Recalculer la valeur effective, selon les sources encore actives : **3 (quête active) > 1 (manuel préexistant) > 2 (rang) > 0** ; préserver l'ordre initial entre `1` et `2` dans les cas hérités. Des sources de quête distinctes doivent pouvoir se cumuler : terminer MQ02 ne révoque pas l'accès donné par une autre mission.
- Quand un droit de quête expire, supprimer **sa source seulement**, recalculer l'accès et resynchroniser les tags `ACCESS_*`. Éviter l'appel brut `access/revoke/*`, qui place le score à 0 et retire le tag sans préserver les autres raisons d'autorisation.
- Dans `access/recalculate_by_rank_self.mcfunction` et sa variante `silent`, le recalcul remet aujourd'hui seulement `2` à `0` puis applique le rang. Protéger `3` de tout écrasement et fournir un chemin de recalcul déterministe après retrait de la source de quête.
- `access/sync_tags_self.mcfunction` transforme un score `0` portant encore son tag `ACCESS_*` en `1`. Le nouvel ordre de synchronisation doit éviter de ressusciter involontairement une autorisation après révocation. **Tests obligatoires** : `3 -> 2`, `3 -> 1`, `3 -> 0`, passages entre personnages, reconnexion, deux quêtes donnant accès à la même zone.
- MQ02 : le droit de quête autorise les Archives dès la présentation de la convocation, puis cesse une fois l'entretien terminé **et le joueur sorti**. MQ03 : autorisation à nouveau reconnue lors du retour avec les renseignements du capitaine ; un Scribe peut l'avoir transmise aux gardes. Conserver les droits permanents.
- Les laissez-passer courts `CAP_PASS_ZONE` / `CAP_PASS_TIMER` restent distincts de `ACCESS_* = 3` ; ne pas les réécrire automatiquement. **Point d'audit** : `CAP_PASS_ZONE` est actuellement un compteur de zone unique, ce qui mérite vérification si deux quêtes chronométrées peuvent se chevaucher.

## 3. Gardes, dialogues, timers

- Accueil conditionnel : criminalité active en premier, puis réputation connue de la Garde, puis variante de race, sans inventer de compétences ni de profession au protagoniste. Les variantes raciales existent aussi aux réputations positive et négative ; la réputation personnelle modifie la tonalité, elle n'efface pas forcément toute référence culturelle.
- Les questions de dialogue restent **répétables** et n'entraînent aucun effet de progression ; seule la réponse de continuation fait avancer l'étape.
- Suspendre le délai de reconduite du garde (`CAP_CTX_TIMER` / contexte réellement actif) pendant un dialogue autorisé, sans supprimer les mécanismes anti-intrusion ; à la sortie, annuler le délai périmé si le passage est accordé, sinon reprendre le comportement défini. Tester les interruptions et les dialogues simultanés.
- PNJ permanents partagés : Sentinelles, Scribe, Archivaire, Althéon. Ne pas lancer des déplacements / téléportations / animations d'entité mondiale divergents pour chaque joueur. Le messager est une apparition scénarisée propre au personnage.

## 4. Personnages Nexus : identité et isolation

- L'UUID Minecraft identifie le **compte** et sert au routage réseau ; l'identifiant Nexus identifie le **personnage RP** et doit déterminer sa progression persistante.
- Ne jamais adopter une seule sauvegarde `UUID -> progression`, puisque plusieurs personnages Nexus d'un même compte partagent cet UUID.
- Faire reposer les états de quêtes et compteurs (`CAP_POINTS_TOTAL`, nouvelles `QUEST_MQ*`, `hcq_*`, `hcc_*`, réputations, accès de quête) sur des données sérialisées/restaurées **par personnage**. Vérifier la politique de persistance de la version Nexus en production, et le correctif QUESTSYNC de la lignée PRE33/PRE34.
- La visibilité des entités et les caches client basés sur l'UUID sont utilisables **seulement si invalidés et resynchronisés** au changement de personnage. Aucune supposition que la fonction « visible seulement au propriétaire » d'EasyNPC distingue déjà les personnages Nexus d'un même compte.
- À chaque sélection de personnage : terminer/annuler proprement le dialogue en cours de l'ancien personnage ; restaurer l'état du nouveau ; recalculer accès ; recharger les quêtes et la disponibilité du messager ; actualiser `!/?`, visibilité des PNJ et waypoint suivi (Xaero). Le changement ne doit octroyer aucune récompense ni valider aucune quête.
- Tester deux comptes en simultané **et** deux personnages alternés du même compte : la seconde situation est essentielle.

## 5. Journal et marqueurs de Clément

- Continuer à utiliser les définitions sous `data/hc_quetes/hcq_quests/*.json` avec `source.kind = SCOREBOARD`, ainsi que `active`, `turn_in`, `completed` et les états `done` des objectifs.
- Ne pas dupliquer les quêtes `core_*` déjà déclarées ; créer des définitions spécifiques MQ01–MQ04 avec des IDs nouveaux, stables, et des objectifs cohérents avec les fonctions du core.
- Conserver les marqueurs au-dessus des PNJ (`!` / `?`) **et** le waypoint de la quête suivie via le pont Xaero. La présence de Xaero's Minimap est une dépendance du pont et elle est confirmée par le propriétaire du serveur ; World Map est également installé.
- À l'issue de MQ03, activer et sélectionner MQ04, positionner le waypoint à **Havre-Fort / secteur portuaire** (coordonnées réelles à retrouver sur la map, ne pas inventer), sans révéler l'île du monolithe.
- Le JAR b6 comporte `QuestVisibility` et `XaeroBridge`, mais leur fonctionnement combiné avec Nexus doit être validé au changement de personnage, pas seulement à l'entrée/sortie du serveur.
- **Vérification de cohérence** : dans le snapshot v16.1, `core_citoyennete.json` est encore classé `type: MAIN` et certains textes parlent de « citoyenneté de la Capitale ». Harmoniser la dénomination « Citoyenneté impériale » et vérifier la classification sans transformer cette quête en prérequis de MQ01.

## 6. Biomes urbains et apparition du messager

- Le pack RC9BC possède `data/indivis/tags/worldgen/biome/city.json`. Ce tag inclut la Capitale, Lion-Port et Havre-Fort, mais **également Avaleiv, Skarnfjord, Urzak-Tor, Ilystära**, etc.
- Par conséquent, **le tag `indivis:city` ne signifie pas « ville impériale »**. Établir une liste / un tag distinct de **lieux habités impériaux éligibles**, issu de la carte politique validée. Utiliser en plus les règles de safezone adaptées.
- Si `CAP_POINTS_TOTAL >= 5` hors de ces lieux, laisser la convocation « en attente » puis présenter le messager lors d'une prochaine entrée dans une zone éligible, sans spawn en donjon, combat, mer ou cité étrangère hostile. Prévoir le cas reconnexion, mort, changement de personnage, et apparition non livrée.

## 7. Recette minimale avant déploiement

1. Résident, prologue terminé, 4 points -> pas de messager ; cinquième point obtenu -> événement en attente en dehors d'une ville, puis messager dans une ville impériale éligible. Pas de citoyenneté obligatoire.
2. Retour en ville étrangère référencée seulement par `indivis:city` -> aucun messager impérial si ville non autorisée.
3. Remise de la lettre -> MQ01 ; présentation aux gardes -> MQ02 ; entretien Scribe/Archivaire -> MQ03 ; retour d'Althéon -> Archives -> MQ04 + waypoint Havre-Fort.
4. Accès Archives : `0 -> 3 -> 0`, `2 -> 3 -> 2`, `1 -> 3 -> 1`. Le cas `3` provenant de deux quêtes exige le maintien de `3` tant qu'une source reste active.
5. Reconduction des gardes : dialogues à plusieurs questions sans minuterie intrusive, pas de téléportation différée après autorisation, retour d'un vrai intrus au comportement normal.
6. Deux joueurs parlent simultanément au Scribe et à l'Archivaire : aucune interruption croisée, aucun mouvement mondial abusif.
7. Deux personnages Nexus d'un même UUID, à des étapes différentes : isoler scores, état MQ, messager, `!/?`, lettre, droits d'accès, waypoint, reprise après reconnexion.
8. Répéter les questions facultatives à volonté sans distribuer d'objet/récompense ni avancer la quête.
9. Vérifier FPS / CPU en ville avec les PNJ normaux, sans rendre obligatoire la nouvelle intégration cosmétique Nexus dans ce chantier.
10. Exécuter tous les tests réellement réalisables ; déclarer séparément les contrôles statiques et les tests en serveur Minecraft réel.

## 8. Alerte de version / intégrité

L'inspection du seul ZIP RC9BC fait apparaître des appels comme `capitale:choice/access/auto_follow_self` et `capitale:justice/intercept/auto_to_tribunal_self` dont les chemins cibles **ne se trouvent pas dans cette archive**. Cela ne démontre pas l'état du serveur si d'autres datapacks fournissent les fonctions ou si le core actif est plus récent. **Avant toute modification des contrôles de passage / de justice, dresser l'inventaire des fonctions effectivement chargées** et résoudre les dépendances sans les inventer.

**Livrable de travail souhaité :** spécification détaillée des modifications, diff exact, banc de tests, plan de migration et retour arrière ; implémentation dans une branche de développement ou copies distinctes, jamais directement sur la production.
