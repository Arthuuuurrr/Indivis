# INDIVIS — PQ03 « Sous le regard du Cœur »
## Dossier consolidé de finalisation narrative et cahier des charges Work
**Date :** 7 novembre 2026  
**Base examinée :** `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2_PQ02_R1_HF8(1).zip`  
**Statut :** scénario consolidé pour intégration ; **aucune modification du core n'est incluse dans ce dossier**.

## 1. Périmètre et priorités
Intégrer PQ03 sans régression de PQ01, PQ02, du prologue, des marqueurs de quête, du journal, de la gestion des personnages Nexus, ni des escortes de Léovic et Gautier. Ne pas retoucher PQ04 ni anticiper la conversation avec Roch Vallet. Éviter toute modification des autres datapacks ou mods avant leur fourniture et vérification.

Priorités, par ordre : (1) identité multijoueur et personnage actif, (2) stabilité des états de quête, (3) intégrité des deux routes A–I et A–J–V, (4) dialogues et conséquences, (5) fluidité et optimisation, (6) cohérence UI/audio/caméra/objectif.

## 2. Ce qui est confirmé, proposé ou non vérifié
**Confirmé par les échanges :** géographie en Y, commerces facultatifs, escorte privée similaire à Léovic/Gautier, dialogues d'Aurèle globalement appréciés, rappel/interception finale, pas de réputation pour le simple choix de route.

**Proposition de barème à confirmer si nécessaire :** réputation Garde du Cœur de −1 à +2, sans bonus automatique ; impression personnelle d'Aurèle séparée, calculée sur les décisions réellement significatives.

**Non vérifié :** exactitude des coordonnées actuelles dans le monde, capacités des mods d'instance privée, protocole exact d'intégration NexusCharacters, compatibilité des menus interactifs avec les autres packs, déclenchement de PQ04 et raccord externe, comportement réellement performant à deux joueurs ou plus.

## 3. Routes et états de jeu
```
                      ┌─ A → B → C → D → E → F → G → H → I
Arrivée Port → A ────┤    Cercle marchand, temps libre ; retour I → A
                      └─ A → J → K → L → M → N → O → P → Q → R → S → T → U → V
                           Ascenseur des Profondeurs ; scène finale
```
- Choix « Cercle marchand » : escorte jusqu'à I, Aurèle se retire physiquement vers A, le joueur explore librement. Il retrouve Aurèle à A pour engager la seconde escorte vers V.
- Choix « Profondeurs » : départ direct A → J → V, sans passage par I.
- Choix « Plus tard » : aucun mouvement, aucun échec et possibilité de reprendre la discussion.
- Discussion en mouvement : pause du vrai mouvement et des timers liés à la progression ; reprise au waypoint/segment interrompu une fois le dialogue fermé, sans téléportation visible.
- En cas d'éloignement du joueur pendant l'escorte : attente et rappel privé sans avancer, sans spam, sans choisir un joueur tiers comme propriétaire.
- À V : arrêt obligatoire, dialogue final, puis validation de PQ03 et départ physique ou retrait invisible de l'instance privée.

### Correspondances techniques héritées du core
- `pq03/v1` : proposition/rencontre ; `v20` : escorte commerces ; `v25` : attente/retour ; `v30` : retour à A et reprise ; `v40` : escorte Profondeurs ; `v50` : dialogue final ; `v100` : quête terminée. Le core prévoit aussi un état d'attente `v10`. **Ne pas écraser ces transitions sans examiner tous leurs consommateurs.**
- Fonctions existantes à examiner : `quest/sous_le_regard_du_coeur/aurele/*`, `escort/*`, `sequence/*`, `npc/aurele/*`, marqueurs/objectifs, `hc_quetes:migration/state/pq03/*`, `hc_quetes:migration/write/pq03/*`.

## 4. Dialogue complet consolidé — unités narratives
**Note de montage :** ces blocs sont un script narratif. Ne pas supposer que chaque réplique exige une fenêtre dédiée. Regrouper les phrases consécutives et éviter les « Continuer » et les fermetures involontaires. Dans les blocs facultatifs, revenir au menu précédent sans rejouer l'introduction.

### A01. Rencontre au premier ascenseur
Aurèle : « Halte. Vous venez du Port, n'est-ce pas ? Ici, vous êtes dans les passages du Cœur. Vous pouvez emprunter les voies ouvertes au public, mais les accès de service restent réservés au personnel autorisé. »

Joueur (questions facultatives) :
- « Pourquoi le Cœur est-il aussi surveillé ? »
- « Que trouve-t-on au-delà des passages publics ? »
- « Vous pourriez m'indiquer le chemin ? »

Aurèle, surveillance : « Parce que ces installations ne sont pas ordinaires. Certains accès desservent des équipements dont vous ne connaissez pas les risques. Les Ingénieurs s'en occupent ; la Garde veille à ce qu'on ne s'y aventure pas sans autorisation. »

Aurèle, au-delà : « Des installations et des accès de service, pour l'essentiel. Je ne suis pas Ingénieur, et je préfère ne pas prétendre maîtriser ce qui ne relève pas de mes fonctions. »

Aurèle, orientation : « Vous avez deux possibilités. Si vous souhaitez découvrir les commerces de la capitale, je peux vous accompagner jusqu'au Cercle marchand. Sinon, nous pouvons rejoindre directement l'ascenseur des Profondeurs. »

Choix :
- A « Je veux bien découvrir le Cercle marchand. » → Aurèle : « Très bien. Suivez-moi, je vais vous montrer le chemin. Vous pourrez prendre votre temps une fois sur place. » → route A–I.
- B « Je préférerais rejoindre les Profondeurs. » → Aurèle : « Comme vous voudrez. Les Profondeurs sont accessibles par un autre passage. Suivez-moi. » → route A–J–V.
- C « J'aimerais encore vous poser quelques questions. » → menu facultatif sans progression.
- D « Je vais prendre un peu de temps avant de me décider. » → retour à l'état d'attente ; Aurèle ne part pas.

**Condition :** aucune variation de réputation pour le choix d'itinéraire.

### B01. Premiers pas — départ d'escorte (commun, adapter « passage » aux deux routes)
Aurèle, en marchant : « Nous allons emprunter l'un des passages principaux. Ils desservent plusieurs secteurs de la capitale ; vous aurez probablement l'occasion de les reprendre. »

Aurèle : « Le plus difficile, au début, c'est de reconnaître les accès qui ne mènent pas aux espaces publics. »

### B02. Le grondement du Cœur — repère D, branche commerces uniquement si le son est bien perceptible
Aurèle : « Vous entendez ce grondement ? On finit presque par ne plus y prêter attention, à force de travailler ici. »

Aurèle : « Il y a pourtant quelque chose d'assez impressionnant à se dire que tout cela était déjà là bien avant nous. »

Question facultative « Vous savez d'où vient ce bruit ? »
Aurèle : « Du fonctionnement des installations, pour l'essentiel. Je saurais difficilement vous en dire davantage. Ce sont les Ingénieurs qui en connaissent les détails. »

Question « Cela ne vous inquiète jamais ? »
Aurèle : « Pas particulièrement. Mais je ne confonds pas l'habitude avec l'absence de danger. C'est aussi pour cela que nous surveillons les accès. »

Question « Vous n'avez jamais cherché à en apprendre davantage ? »
Aurèle : « Si, naturellement. Seulement, la curiosité ne donne pas toutes les compétences. Je préfère encore poser une question à un Ingénieur plutôt que prétendre connaître son métier. »

### B03. Son métier — facultatif pendant les deux escortes
Joueur : « Vous surveillez ce passage tous les jours ? »
Aurèle : « Pas exclusivement. Les affectations varient selon les besoins, mais je connais assez bien ce secteur. »

Joueur A « Cela doit devenir monotone. »
Aurèle : « Certaines journées, oui. Mais une journée tranquille est rarement une mauvaise nouvelle pour quelqu'un chargé de la sécurité. »

Joueur B « Vous devez voir passer beaucoup de monde. »
Aurèle : « Suffisamment pour apprendre à reconnaître ceux qui cherchent leur chemin et ceux qui cherchent à passer là où ils n'ont rien à faire. »

Joueur C « Vous aimez votre travail ? »
Aurèle : « Oui. Pas chaque minute, évidemment. Mais je préfère un travail dont je comprends l'utilité, même lorsqu'il consiste à répéter les mêmes consignes. »

### B04. Discipline et discernement — facultatif pendant les deux escortes
Joueur : « Vous appliquez toujours le règlement à la lettre ? »
Aurèle : « Autant que les circonstances le permettent. Un règlement existe pour une raison, mais encore faut-il comprendre laquelle. »

Questions et suites possibles :
- « Pourtant, il faut parfois savoir faire preuve de souplesse. » → « Bien sûr. Il y a une différence entre quelqu'un qui s'est trompé de passage et quelqu'un qui insiste pour franchir une porte gardée. »
- « La discipline doit être importante dans votre garde. » → « Elle l'est. Mais la discipline ne dispense pas de réfléchir. Un soldat incapable de discernement peut devenir aussi embarrassant qu'un soldat incapable d'obéir. »
- « Je n'aime pas beaucoup qu'on me dise où aller. » → « Je peux le comprendre. Mais tant que vous empruntez les voies publiques, vous n'avez pas à me demander la permission à chaque intersection. »

Prise de position facultative, accordée **une seule fois par personnage et par quête** :
- « Je comprends. La discipline ne vaut pas grand-chose sans discernement. » → réputation Garde du Cœur +1 ; impression Aurèle +1.
- « Il faut parfois savoir faire preuve de souplesse. » → 0 ; 0.
- « Un règlement reste un règlement. » → 0 ; 0.

### B05. La circulation — repère G, branche commerces
Aurèle : « La plupart des gens traversent ces passages sans avoir à se préoccuper de la surveillance. C'est plutôt bon signe, à vrai dire. »

Aurèle : « Lorsque notre présence devient le principal sujet de conversation, c'est généralement que quelque chose ne va pas. »

Joueur : « Vous préférez donc passer inaperçu ? »
Aurèle : « Dans l'exercice de mes fonctions ? Souvent, oui. Je ne suis pas ici pour que les passants s'arrêtent admirer mon uniforme. »

Joueur : « Pourtant, votre équipement ne passe pas vraiment inaperçu. »
Aurèle : « C'est vrai. Disons que l'équipement doit se remarquer davantage que celui qui le porte. »

### B06. Arrivée au Cercle marchand — point I, branche commerces uniquement
Aurèle : « Nous y voilà. Le Cercle marchand. Vous y trouverez de quoi vous équiper, vous approvisionner et probablement dépenser quelques Martins de plus que prévu. »

Joueur, facultatif : « Vous semblez connaître l'endroit. »
Aurèle : « Suffisamment pour savoir qu'il est plus facile d'y entrer avec une bourse pleine que d'en ressortir avec la même somme. »

Aurèle : « Je dois retourner à mon poste. Prenez le temps que vous souhaitez pour découvrir les lieux. Lorsque vous serez prêt à descendre vers les Profondeurs, retrouvez-moi près de l'ascenseur par lequel vous êtes arrivé. Je vous accompagnerai jusqu'au prochain. »

Réponses :
- « Merci pour votre aide. » → « Je vous en prie. Profitez de votre visite. »
- « Vous repartez déjà ? » → « Mon service ne s'arrête pas parce que nous sommes arrivés. Mais je serai disponible à votre retour. »
- « Très bien. À tout à l'heure. » → « À tout à l'heure. »

Aurèle retourne physiquement par I–H–G–F–E–D–C–B–A. Le retour est privé au joueur. Pas de réputation automatique.

### B07. Retrouvailles à A après les commerces
Aurèle : « Vous revoilà. Avez-vous trouvé ce que vous cherchiez ? »

- « Oui, j'ai pu faire quelques achats. » → « Tant mieux. Vous connaissez déjà un peu mieux les lieux. »
- « Pas vraiment, mais j'ai eu le temps de regarder. » → « Ce n'est déjà pas inutile. On ne découvre pas une capitale entière en une promenade. »
- « Je n'ai pas particulièrement apprécié l'endroit. » → « Tous les quartiers ne plaisent pas à tout le monde. Vous aurez d'autres occasions de vous faire une opinion. »

Aurèle : « Souhaitez-vous poursuivre vers les Profondeurs ? »

Choix « Oui » → A–J–V. Choix « J'ai des questions » → menu facultatif. Choix « Plus tard » → attente à A, sans perte de progression.

### C01. Les différences entre quartiers — repère N, branche Profondeurs
Aurèle : « Vous remarquerez vite que les quartiers de la capitale n'ont pas tous le même visage. »

Aurèle : « Les Profondeurs sont assez différentes du Port et des secteurs qui entourent le Cœur. Cela ne signifie pas qu'elles soient moins importantes pour la cité. »

Joueur : « En quoi sont-elles différentes ? »
Aurèle : « Le cadre de vie, les métiers, les moyens dont disposent les habitants... Vous le constaterez par vous-même. »

Joueur : « Vous semblez vouloir éviter de les juger. »
Aurèle : « Parce que je ne vois pas l'intérêt de juger tout un quartier à partir de quelques histoires. Il y a des gens qui y travaillent, qui y vivent et qui n'ont pas demandé qu'on les réduise à leur adresse. »

### C02. Avant l'ascenseur — repère S
Aurèle : « Nous approchons de l'ascenseur. Vous pourrez rejoindre les Profondeurs à partir de là. »

Aurèle : « Un conseil : prenez le temps de vous repérer avant de vous aventurer trop loin. C'est valable dans n'importe quel quartier que l'on découvre pour la première fois. »

Joueur : « Vous pensez que je risque d'avoir des ennuis ? »
Aurèle : « Pas nécessairement. Mais vous venez tout juste d'arriver dans la capitale. Cela suffit à justifier un peu de prudence, vous ne croyez pas ? »

### C03. Arrivée à V — dialogue final obligatoire
Aurèle s'immobilise, se tourne vers le protagoniste ; les déplacements sont suspendus.

Aurèle : « Nous y voilà. Cet ascenseur dessert les Profondeurs. Je vais devoir vous laisser poursuivre seul à partir d'ici. »

Aurèle : « Vous allez découvrir un autre visage de la capitale. Les habitudes et les conditions de vie y sont différentes, mais je préfère vous laisser vous faire votre propre opinion. »

Aurèle : « Les logements y sont généralement plus abordables que dans les quartiers supérieurs. Cela pourrait vous être utile, compte tenu de votre situation. »

Aurèle : « Pour le reste, je vous conseille de prendre vos renseignements auprès des habitants. Ils connaissent leurs rues et leurs habitudes mieux que moi. »

### C04. Menu final de questions facultatives
- « Vous n'allez pas descendre avec moi ? » → Aurèle : « Non. Mon service me retient au Cœur. Et je ne serais pas nécessairement le meilleur guide là-dessous. Mieux vaut demander son chemin à quelqu'un qui connaît vraiment les lieux. »
- « Est-ce dangereux ? » → Aurèle : « Il faut rester prudent, comme partout dans une ville qu'on connaît mal. Je ne vais pas vous présenter tout un quartier comme une menace. »
- « Où pourrais-je trouver un logement ? » → Aurèle : « Demandez aux habitants ou aux personnes qui s'occupent des locations sur place. Je préfère ne pas vous orienter au hasard. »
- « Pourrai-je revenir au Cœur ? » → Aurèle : « Bien sûr, par les passages ouverts au public. Les zones de service restent réglementées, comme je vous l'ai expliqué. »
- « Je crois être prêt à descendre. » → C05.

Les réponses non présentes mot pour mot dans les échanges antérieurs (danger, logement, retour au Cœur) sont **propositions de raccord**, à relire avant la version définitive.

### C05. Position personnelle à la séparation — choix exclusifs
Aurèle : « J'espère que cette traversée vous aura au moins permis de mieux vous repérer. »

- A « Merci de m'avoir accompagné. Je comprends mieux votre travail. » → Aurèle : « Je vous en prie. Vous avez eu raison de demander plutôt que de vous aventurer au hasard. » → réputation +1 ; impression +1.
- B « Merci. Je pense pouvoir continuer seul. » → Aurèle : « Tant mieux. C'était le but. » → 0 ; 0.
- C « Ces restrictions sont tout de même contraignantes. » → Aurèle : « Je peux le comprendre. Mais elles existent pour protéger les personnes autant que les installations. » → 0 ; 0.
- D « Vous passez surtout votre temps à entraver les gens. » → Aurèle : « Je vous ai accompagné pour vous éviter des difficultés, pas pour vous en créer. Vous êtes libre de penser ce que vous voulez de nos consignes. Cela ne dispense personne de les respecter. » → −1 ; −1.

### C06. Conclusion
Aurèle : « Quoi qu'il en soit, je vous souhaite une bonne continuation. Prenez le temps de découvrir la ville avant de décider ce que vous en pensez. »

Joueur : « Merci. Bonne continuation à vous aussi. » / « Peut-être nous reverrons-nous. » / « Au revoir, Aurèle. »

Aurèle : « Au revoir. »

**À la fermeture effective de cette conversation seulement** : appliquer le résultat mémorisé, terminer PQ03, libérer le joueur, permettre le raccord avec PQ04 et faire repartir l'instance privée sans effet visible de téléportation.

### C07. Interception privée d'oubli
Si le joueur commence à quitter V sans avoir terminé C03–C06, déclencher une seule fois :
Aurèle : « Un instant. J'aimerais encore vous dire un mot avant de vous laisser poursuivre. »
Objectif : « Revenir parler à Aurèle avant de poursuivre. »
Conserver le marqueur privé sur Aurèle, puis laisser le joueur revenir à son rythme. Pas de déplacement forcé ni caméra autoritaire. Ne pas re-déclencher la réplique à chaque entrée/sortie de rayon. L'escorte reste réellement arrêtée à V jusqu'à la conclusion.

## 5. Réputation et souvenir — spécification
**Proposition conservée** : actions significatives à deux instants maximum, effets cumulatifs :
1. B04, reconnaissance du discernement : +1 faction, +1 impression ; autres choix 0.
2. C05, remerciement argumenté : +1/+1 ; neutre et critique respectueuse : 0/0 ; mépris explicite : −1/−1.

Plage totale Garde du Cœur : −1 à +2. Impression durable : −1 défavorable, 0 neutre, +1 plutôt favorable, +2 favorable. **Pas de récompense +5 en fin de quête.** Pas de gain pour simplement choisir les commerces, refuser les commerces, compléter PQ03 ou explorer le monde. Chaque décision s'applique une seule fois, même après reconnexion, répétition de dialogue ou changement de personnage Nexus.

L'impression durable est un souvenir privé d'Aurèle pour le personnage actif. La réputation est institutionnelle, séparée. Vérifier les vrais scoreboards/stockages avant toute implémentation ; ne pas créer de doubles compteurs incompatibles avec l'architecture du projet.

## 6. Audit ciblé du core fourni
Les fichiers et extraits suivants ont été directement inspectés dans l'archive :
- `.../aurele/interact_self.mcfunction` : route selon états 0/10/1/20/25/30/40/50/100 ; menus actuels insuffisants.
- `.../aurele/show_choices_self.mcfunction` : propose commerces / explication / report, **pas de départ direct explicitement proposé dans ce menu**.
- `.../escort/start_to_profondeurs_self.mcfunction` : remet le guide à A, définit `NPC_PATROL_STATE 8` et téléporte `npc_aurele_veyrane` à A ; plusieurs sélecteurs non isolés.
- `.../escort/setup/summon_guide_here.mcfunction` : supprime globalement `guide_aurele_coeur`, puis invoque un seul guide avec un **UUID fixe** (`b3330000-0000-4003-8000-000000000501`). Non compatible en l'état avec plusieurs instances parallèles.
- `.../escort/tick.mcfunction` et `move_tick.mcfunction` : parcours des entités via tags globaux et proximités ; risque de mélange des propriétaires.
- `.../escort/next_8.mcfunction` et `next_21.mcfunction` : récupération d'un joueur via `@a[...,sort=nearest,limit=1]`, non via un propriétaire vérifié ; risque de conclure la mauvaise escorte.
- `.../aurele/arrive_commerces_self.mcfunction` : supprime les tags de **tous** les guides correspondants, même d'autres joueurs ; à corriger.
- `.../escort/return_0.mcfunction` : exécute la fin de retour sur **tous** les joueurs à l'étape `pq03/25`, à isoler.
- `.../sequence/aurele_completion_self.mcfunction` : contient `scoreboard players add @s REP_GARDECOEUR 5` et son message : à remplacer par les gains conditionnels sans altérer les autres récompenses de quête.
- `.../aurele/arrive_profondeurs_self.mcfunction` : place bien l'état `pq03/v50`, mais ne remplace pas le vrai dialogue final/interception.
- `.../escort/return_start.mcfunction` : téléporte le guide au waypoint V ; ne pas transposer ce comportement directement à un PNJ visible lors de son départ.
- `.../escort/next_3.mcfunction`, `next_13.mcfunction`, `next_18.mcfunction` : remarques contextuelles existantes, mais répliques anciennes et commandes de son répétées à inspecter.

**Limite de l'audit :** ce dossier ne prouve pas que le mod NPC utilisé permet la création d'une instance réellement privée en réseau. Il faut comparer explicitement aux implémentations de Léovic et Gautier et fournir les dépendances pertinentes à Work.

## 7. Exigences techniques non négociables pour Work
1. **Identifier le propriétaire exact de chaque escorte** : un joueur et un personnage Nexus actif, sans `nearest` comme preuve de propriété. Le point A partagé et les 22 waypoints peuvent rester communs comme données de navigation, mais jamais les états dynamiques.
2. **Isoler PNJ, guide, mouvement, dialogues, voix, cameras, objectifs, marqueurs et interpellations** par propriétaire. Conserver un Aurèle statique accessible aux autres joueurs ; le masquer localement au joueur escorté seulement quand nécessaire.
3. **Respecter la persistance** (déconnexion, changement de dimension, redémarrage serveur, changement de personnage) : ne jamais terminer la quête d'autrui ni supprimer ses entités. Réinitialisation/reprise explicite et sûre si l'instance disparaît.
4. **Optimiser les ticks** : ne pas multiplier les sélecteurs mondiaux, scans à chaque tick, commandes sonores dupliquées ou téléportations coûteuses. Comparer aux coûts des escortes Léovic/Gautier et instrumenter si possible par Spark.
5. **Protéger les dialogues** : pause réelle de l'escorte ; anti-double ouverture ; aucun choix perdu ; aucune fenêtre intempestive ; menus de questions facultatives réentrants ; effets de réputation idempotents.
6. **Respecter le mouvement** : départ direct à J pour les Profondeurs, retour progressif I→A pour les commerces ; aucun passage fictif par le Cercle lors du choix direct ; si obstacle/waypoint manquant, message privé explicite et absence d'avancement de quête.
7. **Garantir la transition PQ03→PQ04** : le rappel final ne doit pas déclencher PQ04 prématurément ; après validation, PQ04 doit rester indépendante et pouvoir commencer avec Roch Vallet dans ses propres conditions existantes.
8. **Préserver les dispositifs du serveur** : indicateurs `!`, journal, objectifs, sons, localisation, choix de race/réputation si déjà intégrés, sans altérer le reste du core.

## 8. Tests de recette demandés
- Joueur A choisit directement les Profondeurs : A→J→…→V, aucun passage par B–I.
- Joueur B visite I, repart librement, Aurèle retourne à A, puis B reprend A→J→V.
- Refus et report répétés à A : aucune progression involontaire, aucune réputation.
- Discussions facultatives en cours de déplacement : arrêt vrai, reprise exacte du segment, pas de mouvement pendant caméra/choix.
- B04 et C05 : +2 max, −1 min, pas de +5 ; aucun double gain via répétition de menu, reconnexion, rechargement.
- V : départ du joueur avant interaction → interpellation privée unique ; objectif maintenu ; conversation complète ensuite → fin correcte.
- Deux joueurs simultanés : chacun a son Aurèle mobile privé, scènes différentes possibles, aucun croisement de dialogue/caméra/guide, aucun déplacement induit chez l'autre.
- Deux personnages Nexus sur un même compte : états, réputation, souvenir, progression et escortes distincts.
- Déconnexion/reconnexion aux états v20, v25, v30, v40, v50 ; redémarrage serveur ; présence/absence des entités ; prévention des fuites.
- Vérification ponctuelle des FPS/TPS lors d'escortes simultanées et ouverture de dialogues.
- Non-régression sur PQ01/PQ02, Léovic, Gautier, prologue et PQ04.

## 9. Livrables attendus de Work
- Audit initial des dépendances et des conflits avant changement ; indiquer les fichiers réellement modifiés.
- Core corrigé complet en ZIP, directement installable et versionné, sans autre datapack intégré par erreur.
- Rapport des changements techniques, état par état et fonction par fonction.
- Journal des tests réalisés : tests automatiques vs tests nécessitant un monde multijoueur réel ; ne pas déclarer « validé » sans exécution.
- Guide d'installation/remplacement, sauvegarde du monde, éventuelles migrations, rollback et commandes de diagnostic.
- Liste explicite des dépendances encore nécessaires (mods, autres datapacks, exemple d'instance privée Léovic/Gautier, logs du serveur) lorsqu'une vérification ne peut pas être faite avec le ZIP fourni.
