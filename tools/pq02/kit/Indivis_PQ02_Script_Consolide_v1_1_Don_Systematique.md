# INDIVIS — PROLOGUE PQ02
## « Le registre du Port » — Script narratif consolidé et cahier de mise en scène

**Version de travail : 1.1 — 1er novembre 2026 ; v1.0 fournie + décision utilisateur sur le versement systématique.**
**Statut :** adaptation éditoriale limitée au don et à ses réponses ; les essais de banc ne constituent pas un essai graphique sur la carte du serveur.
**Destinataires :** écriture, intégration des quêtes et doublage (Clément / équipe Indivis).  
**Base technique consultée :** `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF7.zip`. Les correctifs HF10–HF11 évoqués pour PQ01 ne sont pas présents dans cette archive et ne sont pas réaudités ici.

> **Principe dramaturgique.** Léovic n'est ni un guide touristique ni un officier supérieur. Garde du Port expérimenté, sympathique et apprécié, il connaît les voyageurs et les habitants. Il aide un rescapé amnésique à régulariser sa situation, puis l'oriente vers la suite du prologue. Il peut devenir un personnage récurrent. Son grade reste volontairement indéterminé.

---

## Journal des décisions v1.1 (la décision utilisateur du 01/11/2026 prévaut sur le don v1.0)

1. **Abandon** du bonus automatique de **+5 REP_GARDEPORT** à la conclusion de PQ02.
2. **Répartition définitive** : accueil chaleureux **+1** ; reconnaissance du métier des gardes **+1** ; gratitude marquée pour la bourse **+1** ; mépris envers le don **−1**. L'appréciation exprimée auprès du magistrat est **personnelle seulement** (**impression +1, réputation 0**).
3. **Souvenir synthétique** : quatre décisions intégrées dans un score personnel `CAP_LEOVIC_IMPRESSION`, de **−1 à +4**, utilisé pour les retrouvailles ; ne pas mémoriser chaque formule isolée comme un verdict permanent.
4. **Argent** : 50 Martins d’Or donnés personnellement par Léovic, systématiquement pendant F06 et une seule fois par personnage. Aucun refus, report ou retour pour récupérer la bourse. La réplique sèche D conserve son −1 ; C conserve son +1.
5. **Aucune modification de parcours** : magistrat **à l'air libre sous les Archives** ; ascenseur **tout proche** ; espace public de Léovic **près des tentes** ; instances privées et pauses au dialogue.
6. **Important** : le fichier source de cette édition demeure **HF7** ; ce script n'est pas une preuve que HF11 ou les versions ultérieures emploient les mêmes commandes. Le futur prompt Work doit commencer par inspecter les artefacts effectivement installés.

---

# 1. Décisions de continuité

- **Lieu :** le Port de la **capitale impériale**, et non Lion-Port. Le magistrat se tient **dans un espace ouvert sous les Archives**, sans porte ni intérieur instancié. L'ascenseur se trouve à très courte distance de son poste.
- **Géographie :** Léovic rejoint le joueur près de son affectation habituelle, à côté des **tentes de la Garde du Port**. Le trajet déjà construit est conservé ; aucune nouvelle place, taverne, rue ou position de PNJ n'est prescrite dans ce script.
- **Institutions :** la Garde du Port et la Garde des Profondeurs sont des institutions **distinctes**, autonomes dans leurs quartiers mais pouvant coopérer sur des affaires criminelles. Le protagoniste n'a encore aucune raison de connaître la Garde des Profondeurs : **on n'en parle pas spontanément dans PQ02**.
- **Garde du Cœur :** corps prestigieux associant armée régulière et unités spécialisées. Quelques **Sentinelles du Cœur** sont effectivement visibles dans le Port, notamment aux Archives et aux accès sensibles ou aux quartiers aisés. Leur présence n'est pas présentée comme une anomalie.
- **Progression :** PQ01 est achevée avant l'ouverture de PQ02. L'enregistrement au magistrat confère ou confirme **Visiteur, rang 10**, sans abaisser un rang déjà supérieur.
**Réputation et dons :** l’ancien bonus automatique de +5 est supprimé. A01-A, T04b-A et F06-C donnent chacun +1 (maximum +3) ; F06-D donne −1 (minimum −1). Les réponses neutres, prudentes ou embarrassées valent 0. Léovic verse personnellement 50 Martins d’Or pendant F06, avant la réponse, une seule fois par personnage. Ce don n’est pas une allocation officielle et aucune récupération au retour n’est requise.
- **Personnages :** Léovic, le magistrat et le protagoniste. Le magistrat demeure **sans nom défini** à ce stade ; son caractère est réservé, précis, équitable. Les paroles de Léovic doivent être réellement audibles et mises en scène lorsqu'il intervient auprès du magistrat.
- **Respect du joueur :** réponses respectueuses, prudentes et inquiètes possibles. Aucun malus automatique pour la réserve ou le manque de mémoire ; ne pas forcer le protagoniste à se montrer aimable.

## Convention de lecture

Chaque dialogue possède un identifiant **éditorial** (`P02-A...`, `P02-T...`, `P02-M...`, `P02-F...`). **Ces IDs ne remplacent pas les fonctions ou objectifs existants** : il faudra les mapper au système de dialogue retenu. Les textes précédés de **Léovic**, **Magistrat** ou **Joueur** sont des répliques ; les lignes **[Mise en scène]** et **[Système]** sont des instructions hors-jeu.

- **MENU** = choix consultable ; les branches indiquées « retour » reviennent à ce menu sans reproduire l'introduction.
- **INFO** = sujet facultatif ; son ouverture seule ne confère jamais de réputation.
- **CHOIX-DÉCISION** = première prise de position mémorisée une seule fois ; elle peut modifier réputation et/ou **l’impression personnelle globale de Léovic**. Relecture sans nouvelle récompense.
- **AVANCER** = progression de la quête ; les autres options ne la bloquent pas durablement.
- **IMPRESSION** = score interne personnel de Léovic (distinct de `REP_GARDEPORT`), calculé à partir de quatre décisions de PQ02 ; sa valeur n’est pas nécessairement affichée au joueur.
- **CAM-L**, **CAM-M**, **CAM-3** = caméra sur Léovic, sur magistrat, ou plan à trois. Ce sont des intentions de réalisation, non des commandes Minecraft déjà vérifiées.

---

# 2. Bloc A — Rencontre au poste du Port

**Déclencheur :** PQ01 terminée, joueur proche du Léovic permanent au poste voisin des tentes. À l'acceptation, création / prise en charge de l'instance personnelle de Léovic ; disparition visuelle du permanent **seulement pour ce joueur** le temps de l'escorte. **Caméra :** plan rapproché sur Léovic, avec possibilité d'un plan d'ensemble bref révélant les tentes.

### P02-A01 — Première prise de contact

**Léovic :** « Vous devez être le passager du capitaine Brumeforge. Je me trompe ? »

**Joueur — premier choix (CHOIX-DÉCISION, exclusif)**

- **A — Reconnaissant :** « Oui. Merci d'avoir pris le temps de venir me chercher. » → **P02-A02A** ; **Garde du Port +1, impression Léovic +1** ; premier choix verrouillé.
- **B — Prudent :** « On m'a dit que quelqu'un devait m'attendre. C'est vous ? » → **P02-A02B** ; +0.
- **C — Réservé :** « Qui me le demande ? » → **P02-A02C** ; +0, aucune pénalité.

**P02-A02A — Léovic :** « C'est la moindre des choses. Léovic, de la Garde du Port. On m'a chargé de vous accompagner pour quelques formalités. »

**P02-A02B — Léovic :** « C'est bien moi. Léovic, de la Garde du Port. Le capitaine nous a prévenus de votre arrivée ; je vais vous accompagner auprès des autorités. »

**P02-A02C — Léovic :** « Léovic, de la Garde du Port. Rassurez-vous : je ne viens pas vous arrêter. Il nous faut simplement régler votre situation. »

**Convergence vers P02-A03.** Ces réponses restent réellement différentes ; elles ne doivent pas se transformer en trois messages interchangeables.

### P02-A03 — Le rapport d'Althéon

**Léovic :** « Le capitaine Brumeforge nous a transmis ce qu'il savait de votre sauvetage. Une drôle d'affaire, à ce que j'ai compris. »

**Léovic :** « Mais je ne vais pas vous faire répéter sur ce quai tout ce que vous avez déjà raconté à bord. »

**Léovic :** « Avant de partir, souhaitez-vous éclaircir quelque chose ? »

### P02-A-MENU — Questions de départ (facultatives, retour au menu)

1. « Quelles formalités ? » → **P02-A04**.
2. « Je suis libre de circuler ? » → **P02-A05**.
3. « Que savez-vous exactement de mon sauvetage ? » → **P02-A06**.
4. « En quoi consiste votre travail ? » → **P02-A07**.
5. « Nous pouvons y aller. » → **P02-A08** (**AVANCER**).

**P02-A04 — Léovic :** « Nous devons enregistrer votre arrivée et établir ce que nous pouvons de votre situation. Sans documents, il faut passer par le magistrat. »

**Joueur, sous-menu A04 :**

- « Et si je ne connais même pas mon nom ? » → **A04a**.
- « Quel statut pourra-t-il m'accorder ? » → **A04b**.
- « J'avais une autre question. » → retour **A-MENU**.

**A04a — Léovic :** « Nous commencerons avec ce qui est certain. Vous avez été recueilli par un bâtiment impérial, et le capitaine peut témoigner des circonstances de votre arrivée. Ce n'est déjà pas rien. » → retour **A04** ou **A-MENU**, au choix du joueur.

**A04b — Léovic :** « Probablement celui de Visiteur, du moins pour commencer. Le magistrat confirmera ce qu'il est possible de consigner. » → retour **A04** ou **A-MENU**.

**P02-A05 — Léovic :** « Vous n'êtes pas prisonnier, si c'est ce qui vous inquiète. Mais mieux vaut régulariser votre présence avant de partir à la découverte de la capitale. »

**Joueur :** « Et ensuite ? » / « Revenons à mes autres questions. »

**Léovic, si « Et ensuite ? » :** « Ensuite, vous pourrez circuler dans les secteurs ouverts aux visiteurs. Certains accès restent réglementés, comme partout. » → **A-MENU**.

**P02-A06 — Léovic :** « Vous avez été retrouvé inconscient sur un îlot, puis ramené à bord du dirigeable. Pour le reste, le rapport ne contient pas de miracle : il ne dit rien de ce que vous-même ignorez encore. »

**Joueur :** « Le capitaine vous a parlé de mon amnésie ? » / « J'avais une autre question. »

**Léovic, si amnésie :** « Oui. Et je n'ai aucune raison de vous demander d'inventer une histoire pour combler les blancs. » → **A-MENU**.

**P02-A07 — Léovic :** « Nous maintenons l'ordre dans le Port : quais, entrepôts, arrivées, disputes entre équipages... Il y a suffisamment à faire sans se mêler de tout le reste de la capitale. » → **A-MENU**. **[Système]** Marque le sujet « Garde du Port » comme découvert pour le bloc T.

### P02-A08 — Prêt à partir

**Léovic :** « Vous vous sentez capable de marcher un peu ? »

**Joueur :**

- « Oui. Allons-y. » → **P02-A09**.
- « Je préférerais souffler encore un instant. » → **P02-A08a**.

**P02-A08a — Léovic :** « Prenez votre temps. Je ne vais pas vous faire traverser le quartier à moitié conscient. Faites-moi signe quand vous serez prêt. » **[Système]** Ferme la conversation sans abandon de quête ; à la nouvelle interaction, rouvre **A08** et non la présentation.

**P02-A09 — Léovic :** « Très bien. Suivez-moi, et restez à proximité. Les quais ne sont pas toujours commodes pour quelqu'un qui découvre la ville. » **[Système]** Début d'escorte privée ; passage au bloc T.

---

# 3. Bloc T — Traversée du Port de la capitale

**Rythme voulu :** quelques remarques contextuelles pendant la marche, des silences, et des conversations facultatives lancées **uniquement par interaction du joueur**. Aucune carte inventée : les déclencheurs restent attachés à des portions du trajet existant qu'il faudra repérer dans le monde.

### P02-T01 — L'activité du port (remarque de marche)

**Léovic :** « Entre les cargaisons, les équipages et les voyageurs, il y a toujours quelqu'un pour vous passer sous le nez. Les débardeurs ne s'arrêtent pas volontiers pour laisser passer les promeneurs. »

**[Système]** Débloque la rubrique facultative « Le Port ». Ne **stoppe pas** nécessairement l'escorte ; prévoir audio ou sous-titre si doublage.

### P02-T-MENU — Parler à Léovic pendant qu'il marche

**Déclencheur :** interaction sur **l'instance privée**. Suspend **immédiatement le pathfinding réel** et le suivi du guide, oriente Léovic vers le joueur, ouvre **CAM-L**. Les options sont persistantes pendant PQ02 (dans la limite de leur contexte) :

1. « Vous connaissez bien le Port ? » → **T02**.
2. « Il y a toujours autant de monde ? » → **T03**.
3. « Parlez-moi un peu de votre garde. » → **T04** (après la présentation de sa fonction, ou disponible directement puisque Léovic s'est présenté).
4. « Vous me parliez de ces Sentinelles... » → **T05**, **uniquement si** des Sentinelles du Cœur ont été montrées ou reconnues dans le Port.
5. « Racontez-moi une de vos histoires de patrouille. » → **T06**, débloqué après que le joueur a interrogé Léovic sur son expérience.
6. « Reprenons notre route. » → **T-RESUME**.

### P02-T02 — L'expérience de Léovic

**Léovic :** « À force d'y passer mes journées, encore heureux ! Je connais quelques-uns de ces quais presque aussi bien que ma propre maison. »

**Joueur :**

- « Vous travaillez ici depuis longtemps ? » → **T02a**.
- « Et vous aimez ce travail ? » → **T02b**.
- « Et cette agitation, c'est habituel ? » → **T03** (vrai renvoi vers l'autre branche).
- « J'avais une autre question. » → **T-MENU**.

**T02a — Léovic :** « Suffisamment pour avoir vu changer quelques enseignes, plusieurs capitaines et un nombre incalculable de nouvelles recrues. Certains ne tiennent pas un mois avant de demander leur mutation. »

**Joueur :** « Et vous, vous n'avez jamais songé à partir ? » / « Pourquoi cherchent-ils à partir ? » / « Autre question. »

**Si rester — Léovic :** « J'ai mes habitudes et je connais les gens d'ici. Ce serait dommage de tout quitter pour quelques rues plus tranquilles. »

**Si mutations — Léovic :** « Le bruit, les horaires, les disputes... Et le service au Port n'est pas ce qu'il y a de plus prestigieux. Certains espèrent mieux. Je ne leur en veux pas. »

**[Système]** Après T02a, rendre disponible **T06**, puis retour **T02** ou **T-MENU**.

**T02b — Léovic :** « Oui. Les journées se ressemblent rarement. Il faut parfois de la patience, mais je m'ennuierais sans doute ailleurs. » → retour **T02** ou **T-MENU**.

### P02-T03 — Le Port et ses habitants

**Léovic :** « Presque. Des jours calmes, il y en a, mais le commerce et les voyageurs ne s'arrêtent jamais bien longtemps. »

**Joueur :**

- « Il y a beaucoup d'étrangers ? » → **T03a**.
- « Cela doit compliquer votre travail. » → **T03b**.
- « Vous aimez toute cette agitation ? » → **T02b** (renvoi cohérent).
- « J'avais une autre question. » → **T-MENU**.

**T03a — Léovic :** « Certains passent pour vendre ou acheter, d'autres viennent travailler ou tenter leur chance dans l'Empire. Et beaucoup voyagent simplement d'une province à l'autre. Vous entendrez toutes sortes de langues ici. » → **T03** ou **T-MENU**.

**T03b — Léovic :** « Il faut savoir distinguer une menace véritable de deux marins qui ont simplement trop bu. Si nous arrêtions tous ceux qui élèvent la voix, il nous faudrait construire une deuxième prison. » → **T03** ou **T-MENU**.

### P02-T04 — La Garde du Port et la Garde du Cœur

**Léovic :** « La Garde du Port s'occupe de ce quartier. Nous servons l'Empire, mais nous ne sommes pas l'armée régulière. Notre métier, c'est surtout d'empêcher que les problèmes ordinaires ne deviennent graves. »

**Joueur :**

- « Qui forme alors l'armée impériale ? » → **T04a**.
- « Votre travail est-il moins important que le leur ? » → **T04b** (**CHOIX-DÉCISION institutionnel**, seulement si le joueur formule son avis après la réponse).
- « Vous commandez d'autres gardes ? » → **T04c**.
- « Pourquoi faut-il plusieurs gardes ? » → **T04d**.
- « Une autre question. » → **T-MENU**.

**T04a — Léovic :** « La Garde du Cœur. Elle rassemble l'essentiel de l'armée impériale et plusieurs de ses unités les plus prestigieuses. Vous croiserez certainement des Sentinelles aux abords des lieux surveillés. »

**Joueur :** « Pourquoi sont-ils si réputés ? » / « Vous travaillez parfois ensemble ? » / « Revenons à votre garde. »

**Si réputation — Léovic :** « Leur discipline, leur formation, leur équipement... Certains accomplissent des missions qui dépassent de loin nos patrouilles quotidiennes. »

**Si coopération — Léovic :** « Lorsque les circonstances l'exigent, oui. Pour un différend entre marchands, je préfère encore trouver une solution avec mes collègues. »

**[Système]** La mention des Sentinelles **n'active pas à elle seule T05** si le joueur n'a pas encore rencontré une Sentinelle sur le trajet ; elle lui donne seulement le vocabulaire pour la reconnaître.

**T04b — Léovic :** « Moins prestigieux, peut-être. Mais allez demander aux négociants s'ils aimeraient retrouver chaque matin leurs entrepôts sans surveillance. »

**Joueur — une seule première position sur ce sujet (CHOIX-DÉCISION)**

- **A — Valoriser le service :** « Votre travail me paraît aussi nécessaire que celui des Sentinelles. » → **T04b-A** ; **Garde du Port +1, impression Léovic +1**.
- **B — Neutre :** « Je comprends mieux la différence. » → **T04b-B**, +0.
- **C — Sceptique :** « Mais vous avez moins de responsabilités militaires, non ? » → **T04b-C**, +0.

**T04b-A — Léovic :** « C'est exactement ainsi que je vois les choses. Pas besoin de porter la même armure pour être utile à la cité. »

**T04b-B — Léovic :** « C'est l'essentiel. Nous avons chacun nos devoirs. »

**T04b-C — Léovic :** « Sur ce point, vous avez raison. Et tant mieux : nous ne sommes pas là pour remplacer les soldats du Cœur. »

**[Système]** Après choix, sujet consultable mais **décision verrouillée** ; retour **T04** ou **T-MENU**, sans nouvel octroi de réputation.

**T04c — Léovic :** « Quelques responsabilités, oui. Mais je ne dirige pas le Port, si c'est ce que vous imaginez. Il y a des officiers au-dessus de moi. »

**Joueur :** « Vous préférez le terrain ? » / « Qui vous a chargé de m'accompagner ? » / « Autre question. »

**Si terrain — Léovic :** « Je préfère régler un problème sur place plutôt que de le découvrir trois jours plus tard dans un rapport. »

**Si mission — Léovic :** « Votre arrivée a été signalée à nos services. Il fallait quelqu'un qui connaisse les formalités ; on m'a confié cette tâche. »

**[Système]** Ne lui attribuer **aucun grade fixe** dans cette scène.

**T04d — Léovic :** « Parce que les missions diffèrent. Le Cœur veille sur l'Empire et ses institutions ; nous, nous connaissons ce quartier, ses accès, ses habitants et ses embarras. » → **T04** ou **T-MENU**.

### P02-T05 — Les Sentinelles du Cœur dans le Port (conditionnel)

**Condition :** un point sensible réellement traversé présente visiblement une Sentinelle, ou une patrouille identifiable est effectivement présente. Une courte observation facultative de Léovic peut débloquer le sujet :

**Léovic, remarque contextuelle :** « Vous voyez ces gardes ? Ce sont des Sentinelles du Cœur. Elles surveillent certains bâtiments et les accès qui demandent une autorisation particulière. »

**Joueur, lors d'une interaction ultérieure :** « Vous me parliez de ces Sentinelles... »

**Léovic :** « Vous en verrez près des Archives, entre autres endroits sensibles. Nous continuons à assurer l'ordre dans le reste du quartier ; leurs responsabilités sont plus ciblées ici. »

**Joueur :**

- « Elles ne font pas partie de votre garde ? » → **T05a**.
- « Pourquoi leur confier ces accès ? » → **T05b**.
- « Elles ont l'air mieux équipées. » → **T05c**.
- « Revenons aux autres sujets. » → **T-MENU**.

**T05a — Léovic :** « Non. Deux corps différents. Et il nous arrive naturellement de travailler ensemble quand la situation le demande. » → **T05** ou **T-MENU**.

**T05b — Léovic :** « Certains lieux abritent des fonds précieux ou accueillent des personnes importantes. On n'y entre pas aussi librement que sur les quais. » → **T05** ou **T-MENU**.

**T05c — Léovic, amusé :** « Il faut reconnaître qu'elles ont fière allure. Mais une belle armure ne rend pas les rondes moins longues. » → **T05** ou **T-MENU**.

### P02-T06 — Une histoire de patrouille (optionnelle)

**Léovic :** « Un jour, deux négociants se sont disputés pendant une bonne heure au sujet d'une cargaison. Ils avaient tous les deux des papiers parfaitement en règle. »

**Joueur :**

- « Comment avez-vous résolu l'affaire ? » → **T06a**.
- « L'un des deux mentait ? » → **T06b**.
- « Cela vous arrive souvent ? » → **T06c**.

**T06a — Léovic :** « Nous avons fini par vérifier les marques sur les caisses. Les deux avaient raison... mais ils ne parlaient pas de la même cargaison. »

**T06b — Léovic :** « Pas du tout. Ils s'étaient trompés de cargaison et étaient trop occupés à se disputer pour s'en rendre compte. »

**T06c — Léovic :** « Plus souvent que je ne le souhaiterais. Et je vous épargne les histoires d'inventaire et de taxes. »

**Conclusion commune — Léovic :** « Enfin, je préfère cette querelle-là à une vraie affaire criminelle. Au moins, personne n'a été blessé. » → **T-MENU**.

### P02-T-RESUME — Reprendre la route

**Joueur :** « Reprenons notre route. »

**Léovic :** « Bien. Restons ensemble, nous approchons. »

**[Système]** Fermeture du dialogue, restitution caméra, réactivation du pathfinding depuis **l'étape de trajet effectivement suspendue**, et non depuis le début. Le guide et le PNJ restent immobiles tant que le menu est ouvert. À une déconnexion, conserver étape, état du dialogue et position de reprise : **jamais de progression silencieuse**.

### P02-T07 — Approche du magistrat (obligatoire)

**[Mise en scène]** Arrêt précis sur le parcours existant, sous les Archives ou à leur approche immédiate. Le magistrat est visible dans **un espace ouvert**.

**Léovic :** « Nous approchons. Il y a une chose que je préfère vous dire avant de vous présenter. »

**Léovic :** « Le magistrat vous demandera ce qu'il peut établir de votre identité et de votre arrivée. Vous n'avez pas à inventer des réponses pour combler vos pertes de mémoire. »

**Joueur :**

- « Et s'il refuse de m'inscrire ? » → **T07a**.
- « Vous pensez qu'il ne me croira pas ? » → **T07b**.
- « Je comprends. Allons-y. » → **T07c**.

**T07a — Léovic :** « Nous examinerons alors les autres possibilités. Mais n'imaginons pas le pire avant même d'avoir parlé. Le capitaine peut attester votre arrivée. » → **T07c**.

**T07b — Léovic :** « Je ne vois pas pourquoi il vous accuserait de mentir. Votre situation est inhabituelle ; son travail est justement de la consigner correctement. » → **T07c**.

**T07c — Léovic :** « Bien. Je reste avec vous pendant l'entretien. »

**[Système]** Transition au bloc M ; ne jamais faire disparaître Léovic ni le faire repartir durant l'inscription.

---

# 4. Bloc M — Entretien sous les Archives

**Lieu :** poste du magistrat, dans un espace **ouvert**, sous les Archives. Aucun franchissement de porte. **Mise en scène :** ouverture en **CAM-3**, conversation principalement en **CAM-M**, contrechamp **CAM-L** lorsque Léovic intervient. Éviter une coupe pour chaque phrase très courte. Le protagoniste conserve sa possibilité de répondre ; aucun PNJ ne se déplace pendant la scène.

### P02-M01 — Présentation du dossier

**Magistrat :** « Léovic. Je suppose que vous ne m'amenez pas une simple déclaration de cargaison. »

**Léovic :** « Pas cette fois. Voici la personne recueillie par le dirigeable du capitaine Brumeforge. Vous avez dû recevoir son rapport. »

**Magistrat :** « En effet. Un cas peu ordinaire. »

**Magistrat :** « Retrouvé inconscient sur un îlot, sans document d'identité, et incapable d'expliquer ce qui s'est passé auparavant. C'est bien cela ? »

**Léovic :** « Ce sont les circonstances confirmées par le capitaine. Pour le reste, mieux vaut entendre directement notre visiteur. »

**Magistrat, au joueur :** « Très bien. Nous allons avancer avec ce que nous savons, et signaler clairement ce qui demeure incertain. »

### P02-M02 — L'identité

**Magistrat :** « Que pouvez-vous me déclarer au sujet de votre identité ? »

**Joueur :**

- **A :** « Je ne me souviens presque de rien. » → **M02a**.
- **B :** « Je connais le nom sous lequel je me présente, mais je ne peux rien prouver. » → **M02b**.
- **C :** « Le capitaine vous a déjà raconté ce qu'il savait. » → **M02c**.

**M02a — Magistrat :** « Je vois. Je ne vais pas vous demander de retrouver aujourd'hui des souvenirs qui vous échappent. Il nous faudra noter les inconnues comme telles. »

**M02b — Magistrat :** « C'est une distinction importante. Nous pouvons consigner le nom que vous déclarez ; cela ne signifie pas que toutes les pièces permettant de l'authentifier sont réunies. »

**M02c — Magistrat, ferme sans hostilité :** « Son rapport établit les circonstances du sauvetage. Il ne peut pas répondre à toutes les questions qui vous concernent personnellement. »

**Convergence vers M03.**

### P02-M03 — Provenance

**Magistrat :** « Savez-vous au moins d'où vous veniez avant votre sauvetage ? »

**Joueur :**

- « Non. Je n'en ai aucun souvenir. » → **M03a**.
- « J'aimerais pouvoir vous répondre. » → **M03a**.
- « Est-ce indispensable pour m'inscrire ? » → **M03b**.

**M03a — Magistrat :** « Cette information restera donc indéterminée pour le moment. »

**M03b — Magistrat :** « Elle nous serait utile, dans un cas ordinaire. Mais nous ne pouvons pas rejeter les faits connus sous prétexte que les autres nous manquent. »

### P02-M04 — Témoignage sur l'accompagnement (CHOIX-DÉCISION)

**Magistrat :** « Léovic vous a accompagné jusqu'ici. Tout s'est-il déroulé correctement ? »

**Joueur :**

- **A — Reconnaissance :** « Oui. Léovic m'a beaucoup aidé depuis mon arrivée. » → **M04a**, **Garde du Port +0, impression Léovic +1** ; valorise l’aide personnelle, sans représenter un service rendu à toute l’institution.
- **B — Neutre :** « Oui, aucun problème. » → **M04b**, +0.
- **C — Réservé :** « J'aurais préféré ne pas avoir à passer par toutes ces formalités. » → **M04c**, +0.

**M04a — Magistrat :** « C'est entendu. » **Léovic, sobrement :** « J'ai simplement fait mon travail. »

**M04b — Magistrat :** « Très bien. »

**M04c — Magistrat :** « Je comprends. Mais elles évitent que votre présence soit laissée dans une situation incertaine. » **Léovic :** « Au moins, vous n'aurez pas à recommencer toute la procédure demain. »

**[Système]** Ce point de décision n’est proposé **qu’une fois**, puis remplacé par un rappel non rémunérateur. Il n’attribue **aucun** point à `REP_GARDEPORT`, y compris pour M04a ; seul le souvenir général de Léovic est affecté.

### P02-M05 — Questions avant la décision (facultatives)

**Magistrat :** « Je pense pouvoir déterminer votre statut. Souhaitez-vous une précision avant que je procède à l'inscription ? »

**MENU M :**

1. « Que deviendra mon identité si je ne retrouve pas la mémoire ? » → **M05a**.
2. « Que signifie exactement être Visiteur ? » → **M05b**.
3. « Et si je retrouve mes souvenirs ? » → **M05c**.
4. « Quelle place le rapport du capitaine occupe-t-il dans mon dossier ? » → **M05d**.
5. « Vous pouvez poursuivre. » → **M06** (**AVANCER**).

**M05a — Magistrat :** « Nous conserverons les faits attestés et les renseignements que vous déclarez. Si d'autres éléments apparaissent, le dossier pourra être complété. »

**Joueur :** « Je serai donc officiellement un inconnu ? » / « Autre question. »

**Magistrat, si inconnu :** « Votre identité demeure incomplètement établie. C'est différent d'une absence totale d'existence administrative : nous pouvons enregistrer votre présence. » → **MENU M**.

**M05b — Magistrat :** « Le statut de Visiteur reconnaît votre présence dans l'Empire et vous autorise à circuler dans les secteurs ouverts à ce statut, sans vous donner accès à tous les lieux. »

**Joueur :** « Et si je veux m'établir ici ? » / « Autre question. »

**Magistrat, si rester :** « Il faudra alors envisager une demande de Résidence, avec ses propres conditions. Vous n'avez pas à prendre cette décision aujourd'hui. » → **MENU M**.

**M05c — Magistrat :** « Alors vous pourrez présenter les renseignements retrouvés. Nos registres ont précisément vocation à être complétés lorsque les faits évoluent. »

**Léovic, avec un demi-sourire :** « Voilà qui rassurera notre visiteur : même les registres peuvent changer d'avis. »

**Magistrat, légèrement amusé :** « Les registres, non. Ceux qui les remplissent, parfois. » → **MENU M**.

**M05d — Magistrat :** « Il atteste votre sauvetage par un bâtiment impérial. C'est une pièce utile, mais elle ne saurait certifier des événements dont l'équipage n'a pas été témoin. » → **MENU M**.

### P02-M06 — Décision et enregistrement

**Magistrat :** « Au vu des circonstances attestées, je vais consigner votre arrivée et vous reconnaître le statut de Visiteur. Les éléments de votre identité que nous ne pouvons pas confirmer resteront signalés comme tels. »

**[Mise en scène]** Le magistrat consulte ou complète son registre, **sans imposer une animation ni un meuble non présents** ; les personnages restent en place. 

**[Système — instant de validation]** Inscription confirmée : `CAP_RANGSOCIAL` passe à 10 **seulement s'il est inférieur à 10** ; ne pas rétrograder un rang existant. Mettre à jour la progression de PQ02 vers l'étape « Retourner voir Léovic / passage vers l'ascenseur », selon le schéma final. Cet événement est **idempotent**.

**Magistrat :** « Votre arrivée est enregistrée. Pour la Couronne, vous êtes désormais reconnu comme Visiteur de la capitale. »

**Joueur :**

- « Merci. » → **M06a**.
- « C'est tout ? » → **M06b**.
- « Je ne sais toujours pas ce que je vais faire à présent. » → **M06c**.

**M06a — Magistrat :** « Je vous en prie. Je vous souhaite de retrouver les réponses qui vous manquent. »

**M06b — Magistrat :** « Pour aujourd'hui, oui. Votre situation est particulière, mais cela ne justifie pas de compliquer ce qui peut être établi. »

**M06c — Magistrat :** « Cela dépasse les fonctions de ce registre. Mais vous disposez désormais d'une situation reconnue : c'est un commencement. »

**Convergence vers M07.**

### P02-M07 — La sortie de l'entretien, sans porte

**Léovic :** « Eh bien, voilà déjà une bonne chose de faite. Vous voyez, ce n'était pas si terrible. »

**Joueur :**

- « Vous aviez raison. » → **M07a**.
- « J'espère ne pas recommencer de sitôt. » → **M07b**.
- « Et maintenant ? » → **M07c**.

**M07a — Léovic :** « Cela m'arrive, de temps à autre. Mais ne le répétez pas trop à mes collègues. »

**M07b — Léovic :** « Croyez-moi, je vous comprends. Même quand on connaît les procédures, on apprécie les journées sans paperasse. »

**M07c — Léovic :** « Maintenant, il ne nous reste que quelques pas. L'ascenseur est juste à côté. »

**Conclusion commune — Léovic :** « Venez, je vais vous montrer où poursuivre votre chemin. »

**[Système]** Courte reprise d'escorte vers l'ascenseur **voisin**. Aucune nouvelle promenade ou longue série de répliques. Ne pas faire référence à une porte franchie ou à la sortie d'un bureau fermé.

---

# 5. Bloc F — Ascenseur, don et séparation

**Lieu :** proximité immédiate de l'ascenseur, toujours dans le secteur des Archives. **Caméra :** plan sur Léovic, puis plan large pour clore la scène et rendre le contrôle au joueur. Aucun trajet additionnel inventé.

### P02-F01 — Découvrir le Cœur

**Léovic :** « Voilà l'ascenseur. Il vous permettra de rejoindre le Cœur, où vous découvrirez une autre partie de la capitale. »

**Joueur — MENU F (questions de séparation) :**

1. « Vous ne m'accompagnez pas ? » → **F02**.
2. « Que vais-je trouver là-haut ? » → **F03**.
3. « Où pourrai-je vous retrouver ? » → **F04**.
4. « Et mon statut de Visiteur, qu'est-ce que ça change ici ? » → **F05**, facultatif et **raccourci si le magistrat l'a déjà expliqué**.
5. « Je crois pouvoir continuer seul. » → **F06**.

**F02 — Léovic :** « J'aimerais vous en montrer davantage, mais mes fonctions m'attendent au Port. Et mes collègues finiraient par remarquer mon absence. » → **MENU F**.

**F03 — Léovic :** « Des institutions, de grandes demeures, et davantage de soldats du Cœur. L'atmosphère n'est pas tout à fait celle des quais : un peu plus solennelle, disons. »

**Joueur :** « Vous préférez rester ici ? » / « J'avais une autre question. »

**Léovic, si préférer :** « Je connais mon quartier et ses habitants. Cela me convient très bien. » → **MENU F**.

**F04 — Léovic :** « Vous vous souvenez de notre première rencontre, près des tentes de la garde ? C'est mon poste habituel. Je n'y reste pas à chaque instant, mais mes collègues sauront vous orienter. »

**Joueur :** « Je pourrai revenir vous voir ? » / « J'avais une autre question. »

**Léovic, si revoir :** « Bien entendu. Si je peux vous renseigner, je le ferai volontiers. » → **MENU F**.

**F05 — Léovic :** « Vous pourrez circuler dans les secteurs ouverts aux visiteurs, mais pas franchir tous les accès surveillés. Si vous souhaitez vous installer durablement ici, il faudra voir plus tard pour la Résidence. »

**[Système]** Si **M05b** a déjà été parcouru, Léovic répond plus brièvement : « Le magistrat vous l'a expliqué : vous êtes enregistré et libre de circuler dans les secteurs qui vous sont ouverts. » → **MENU F**.

### P02-F06 — Le geste de Léovic

**Léovic :** « Ah, attendez. J'allais oublier une chose. »

**[Mise en scène / versement]** Il présente puis remet une petite bourse. Les 50 Martins sont versés à l’entrée dans F06, avant le choix A/B/C/D, une seule fois par personnage Nexus. Pas de cinématique longue ni de geste imposé sans animation cohérente.

**Léovic :** « Vous êtes arrivé ici sans rien. Et avec ce qui vous est arrivé, j'imagine que trouver de quoi manger demain vous paraît déjà compliqué. »

**Léovic :** « Tenez. Cinquante Martins. Ce n'est pas une fortune, mais cela devrait vous aider à commencer. »

**Joueur — prise de position initiale exclusive (`CAP_PQ02_DEC_DON`) :**

- **A — Curieux :** « Vous me donnez votre propre argent ? » → **F06a** ; réputation **0**, impression **0**.
- **B — Embarras :** « C’est généreux, mais je ne sais pas quoi dire. » → **F06b** ; réputation **0**, impression **0**. **[Adaptation v1.1]**
- **C — Reconnaissance marquée :** « Je n'oublierai pas votre générosité, Léovic. » → **F06c** ; **Garde du Port +1, impression Léovic +1**.
- **D — Réponse sèche :** « Je ne demandais pas votre charité. » → **F06d** ; **Garde du Port −1, impression Léovic −1** ; les 50 Martins restent acquis. **[Formulation utilisateur]**

**F06a — Léovic :** « Oui, ce sont mes propres pièces. Ce n'est pas une aide de l'administration ; voyez-y simplement un coup de main. »

**[Enchaînement v1.1]** Après F06a, passer à **F07**, sans choix supplémentaire d’acceptation ou de report.

**F06b — Léovic :** « Vous n’avez pas à me dire quoi que ce soit. C’est un coup de main, de bon cœur. » **[Adaptation v1.1]**

**[Enchaînement v1.1]** Après F06b, passer à **F07**, sans choix supplémentaire d’acceptation ou de refus.

**F06c — Léovic :** « Ce n'est pas grand-chose. Faites-en bon usage. » → **F07**.

**F06d — Léovic**, surpris, perd brièvement son sourire : « Je ne cherchais pas à vous offenser. Je pensais simplement vous rendre service. »

**Léovic**, plus réservé : « Enfin, vous êtes libre d'en penser ce que vous voulez. » → **F07**. Les 50 Martins déjà versés restent acquis ; son attitude est mémorisée dans le bilan global, sans transformer Léovic en adversaire.

**F06 — [Système]** Donner systématiquement les 50 Martins à l’entrée dans la scène, selon le mécanisme monétaire du core ; verrouiller `CAP_LEOVIC_GIFT` pour le personnage Nexus actif. Ne jamais appeler un cadeau au retour. Le versement ne modifie aucune réputation : C donne +1/+1 et D −1/−1 uniquement lors du choix initial.

**[Compatibilité F06-REPORTER]** Cette ancienne branche est retirée du dialogue. Une sauvegarde en cours qui y était arrêtée reprend à F07, avec versement unique si nécessaire et sans rejouer sa décision.

**[Système — verrouillage]** La prise de position A/B/C/D et ses effets ne sont jamais rejoués. Fermer, reprendre ou recharger F06/F07 ne verse pas une seconde bourse. Une quête déjà terminée avant cette version ne reçoit pas de rattrapage automatique à la connexion ; son historique de réputation et de récompenses est conservé.

### P02-F07 — Au revoir

**Léovic :** « Bien. Je crois que nous avons fait le tour de ce qui me concernait. »

**Léovic :** « Je vous souhaite de retrouver la mémoire et, surtout, de trouver votre place quelque part. Ici ou ailleurs. »

**Joueur :**

- « Merci pour tout, Léovic. » → **F07a**.
- « Peut-être à une prochaine fois. » → **F07b**.
- « Je ferai de mon mieux. » → **F07c**.

**F07a — Léovic :** « Je vous en prie. Prenez soin de vous. »

**F07b — Léovic :** « Qui sait ? Nous nous croiserons peut-être dans des circonstances moins étranges. »

**F07c — Léovic :** « C'est déjà beaucoup. Bonne continuation. »

**[Système]** Terminer PQ02 une seule fois : étape terminale, **aucun bonus automatique de réputation**, autres récompenses non liées à la réputation déjà prévues par le core **préservées** et non doublées. Finaliser l'impression générale de Léovic à partir du cumul de ses quatre décisions. Déverrouiller la suite normale du prologue. Restaurer la caméra et le mouvement du joueur. Dissiper l'instance personnelle discrètement. L'instance publique du poste près des tentes n'a jamais cessé d'exister pour les autres joueurs.

---

# 6. Après PQ02 — Léovic permanent, personnage récurrent

**Déclencheur :** le joueur peut revenir au poste habituel près des tentes pour une salutation fondée sur l’impression générale et des questions pratiques. Ce retour est facultatif et ne conditionne aucune récompense de PQ02 ; il ne propose aucune bourse.

**Impression personnelle :** additionner, une seule fois par décision, les modifications prévues à **A01 (+1 si réponse A)**, **T04b (+1 si réponse A)**, **M04 (+1 si réponse A)** et **F06 (+1 si C ; −1 si D)**. Le cumul est distinct de la réputation. Exemple demandé : un protagoniste à **+2** avant d'être méprisant au don finit à **+1**, donc Léovic conserve une **impression favorable**, bien qu'il ait été blessé sur le moment. Inversement, une attitude intégralement neutre puis méprisante termine à **−1**, soit une **impression défavorable**.

| Total personnel | Impression générale | Salutation ultérieure de Léovic |
|---|---|---|
| **+3 ou +4** | Très favorable | « Ah, vous voilà ! Je suis content de vous revoir. Alors, comment se passent vos premiers jours dans la capitale ? » |
| **+1 à +2** | Favorable | « Tiens, une visite que je n'attendais pas ! Comment allez-vous depuis notre dernière rencontre ? » |
| **0** | Neutre | « Bonjour ! Vous êtes bien le rescapé du dirigeable de Brumeforge ? Comment vous portez-vous ? » |
| **−1** | Défavorable | « Ah, vous revoilà. J'espère que les choses se passent mieux pour vous. Que puis-je faire pour vous ? » |

Les lignes ci-dessus ne sont que les **salutations initiales** : Léovic reste professionnel, cordial par tempérament, et son impression pourra changer dans de futures quêtes. Ne pas mémoriser une insulte ordinaire comme un **souvenir permanent isolé** : c'est précisément la synthèse globale qui détermine son attitude.

### P02-POST-DON — Ancienne offre reportée supprimée (compatibilité uniquement)

**[Décision utilisateur v1.1]** Aucun menu de récupération n’est présenté après PQ02. Les anciennes sauvegardes pointant vers POST-DON sont redirigées vers le menu pratique.

**[Menu]** Les questions pratiques demeurent ; aucune option de don n’est ajoutée.

**[Compatibilité]** L’ancien identifiant POST-DON reste réservé pour les sauvegardes, sans réplique ni versement.

**[Aucune réponse d’acceptation ou de report après la quête.]**

- Aucun versement tardif : les 50 Martins ont été reçus pendant F06.
- Aucun recalcul tardif de réputation ou d’impression.

Les retrouvailles ne rouvrent ni F06/F07 ni les récompenses. La mémoire globale de Léovic reste déterminée par les quatre décisions initiales.

**[Non écrit pour cette version]** Éventuelles missions de Léovic et évolution de leur relation dans d'autres quêtes. Ne pas les inventer uniquement pour remplir PQ02.

---

# 7. Plan des états et conséquences

### 7.1. Effets des décisions

| Scène | Réputation de la garde | Impression de Léovic | Déclencheur unique |
|---|---:|---:|---|
| **A01** | **+1** | **+1** | Accueil chaleureusement reconnaissant |
| **T04b** | **+1** | **+1** | Valoriser le travail de la Garde du Port |
| **M04** | **0** | **+1** | Remercier Léovic devant le magistrat |
| **F06 — C** | **+1** | **+1** | Gratitude sincère pour le don |
| **F06 — D** | **−1** | **−1** | Mépris affiché envers le don |
| **F07** | **0** | **0** | Fin de PQ02 : **suppression du +5 historique** |

Les autres réponses, dont l’embarras B, n’apportent aucun point. C et D s’excluent mutuellement. Le versement des 50 Martins est systématique et ne constitue pas une décision récompensée.

### 7.2. États et garde-fous

- **Décisions verrouillées une fois** : `CAP_PQ02_DEC_ACCUEIL` (A01), `CAP_PQ02_DEC_GARDE` (T04b), `CAP_PQ02_DEC_MAG` (M04), `CAP_PQ02_DEC_DON` (F06). Les sous-menus informatifs restent consultables sans déclencher de nouveau changement.
- **Mémoire globale** : `CAP_LEOVIC_IMPRESSION` cumule les quatre prises de position, de **−1 à +4** ; elle n'est pas déduite de la réputation institutionnelle.
- **Sujets débloqués** : `CAP_PQ02_SUJ_SENT` (Sentinelles réellement aperçues), `CAP_PQ02_SUJ_ANECD` (expérience de Léovic déjà abordée).
- **Inscription administrative** : M06 accorde **Visiteur 10** uniquement si le rang est inférieur, une seule fois, sans rétrogradation.
- **Bourse personnelle** : `CAP_LEOVIC_GIFT` marque le don déjà versé pendant F06. L’ancien état d’offre reste compatible en sauvegarde mais n’ouvre aucun menu tardif ; les 50 Martins ne sont jamais versés deux fois.
- **Progression** : A09 commence l'escorte privée ; F07 termine PQ02 sans octroyer de réputation supplémentaire. Après F07, seul le Léovic public demeure au poste.

**Ces noms sont des propositions éditoriales** et non des IDs imposés au datapack. Réutiliser les mécanismes existants du core actuel s'ils assurent déjà ces fonctions, et éviter les préfixes temporaires exclus des sauvegardes Nexus (`CAP_CTX_`, `CAP_QDIALOG_`, etc.).

### 7.3. Scénarios de cohérence

| Profil de choix | Réputation PQ02 | Impression Léovic |
|---|---:|---:|
| Toujours neutre | **0** | **0** — neutre |
| Chaleureux à l'accueil, valorise la garde, remercie pour le don | **+3** | **+3** — très favorable |
| Chaleureux à l'accueil, valorise la garde, puis méprisant envers le don | **+1** | **+1** — favorable |
| Neutre partout, puis méprisant envers le don | **−1** | **−1** — défavorable |
| Chaleureux, valorise la garde, témoigne au magistrat, puis méprisant | **+1** | **+2** — favorable |

Un joueur à **+2 d'impression** avant F06-D termine à **+1** : la dernière réplique n'efface donc pas les interactions chaleureuses précédentes. Le même choix agressif après une conduite neutre aboutit, lui, à une impression négative.

**Attention :** les noms `CAP_PQ02_DEC_*` et `CAP_PQ02_SUJ_*` sont de **nouvelles propositions** pour cette spécification, pas des objectifs ou fonctions déjà présents dans le datapack. Les préfixes exclus de la politique de persistance Nexus (`CAP_CTX_`, `CAP_QDIALOG_`, etc.) doivent être évités pour les décisions permanentes. Valider une fois sur la version actuelle de Nexus l'isolation des scores `REP_`/`CAP_` entre personnages.

### Objectifs et transitions existants à préserver

La version consultée comporte déjà des étapes PQ02 réparties entre l'offre, l'escorte, le magistrat, le retour vers l'ascenseur, la fin de quête et les interactions après celle-ci. Elle dispose notamment de `CAP_RANGSOCIAL`, `REP_GARDEPORT`, `CAP_LEOVIC_GIFT` et de récompenses de compétences. **La proposition ci-dessus ne réclame aucun changement de numérotation définitif** avant le raccord avec les fonctions actuelles : `hc_quetes:migration` reste l'autorité à prendre en compte.

---

# 8. Cahier technique de réalisation (sans implémentation dans cette version)

## 8.1. Isolation et interruption de parcours

1. L'interaction avec le Léovic public **initie** la quête ; une **instance privée** prend l'escorte. Un joueur qui n'a pas lancé PQ02 continue de voir le Léovic public.
2. Toute interaction du joueur accompagné avec **son** Léovic ouvre une conversation et **annule ou suspend immédiatement l'objectif réel de pathfinding EasyNPC**. Geler uniquement le marqueur de guidage est insuffisant.
3. Durant les questions et sous-questions, suspendre le mouvement du PNJ **et** du guide, sans expiration d'un délai qui le remettrait en marche automatiquement.
4. En choisissant « Reprenons notre route », fermer l'UI et la caméra, puis réactiver le déplacement à l'étape courante (points de passage existants).
5. Une déconnexion ou un changement de personnage doit geler, terminer proprement ou restaurer la scène sans modifier le parcours d'autrui, ni attribuer des points ou monnaies par erreur.
6. L'isolement de PQ01 ne suffit pas comme preuve de PQ02 : tester explicitement deux joueurs en escorte et deux personnages Nexus d'un même compte.

## 8.2. Caméra et scènes à trois personnages

- **A et T :** cadrage sur Léovic, idéalement épaule du protagoniste à environ 30° lorsque la géométrie s'y prête. Éviter caméra intrusive qui traverse les tentes, passants, piliers ou sol.
- **M :** ouverture à trois, puis focalisation sur le magistrat ; changement vers Léovic uniquement quand son intervention le justifie. Ne pas couper mécaniquement à chaque ligne ; privilégier petits changements de plan sur des échanges significatifs.
- **Questions du joueur :** caméra stable, options visibles et interaction fluide ; ne jamais redémarrer le pathfinding à l'apparition des choix.
- **Sortie de dialogue :** retour fiable de la caméra au joueur, même si le joueur quitte, ferme ou interrompt l'interface ; éviter les caméras persistantes après changement de personnage.

## 8.3. Reprise de quête et récompenses

- Sauvegarder par personnage les quatre décisions (A01, T04b, M04, F06), les sujets découverts, le point de passage, l’étape de l’entretien, le versement unique des 50 Martins et la fin de PQ02.
- Récompenses **idempotentes** : chaque décision et chaque achèvement ne donnent leur bonus qu'une fois. Les sous-menus répétables ne doivent jamais appeler le gain de réputation.
- L’ancien bonus +5 est retiré à sa source et jamais compensé ailleurs. Les 50 Martins sont versés systématiquement pendant F06, avant la réponse, indépendamment des effets de C ou D.
- Ne pas confondre la réputation institutionnelle et l’impression personnelle de Léovic. M04 peut améliorer son impression sans augmenter `REP_GARDEPORT` ; F06-D entraîne un malus institutionnel de −1 et personnel de −1. Aucun point n’est attribué pour avoir simplement terminé PQ02.
- Ne pas utiliser l'UUID du joueur comme unique clé d'historique **si** Nexus permet plusieurs personnages sous ce même compte ; confirmer la persistance exacte de la version installée avant l'intégration.

---

# 9. Checklist de validation éditoriale et technique

**Narration**

- [ ] Aucun dialogue ne situe PQ02 à Lion-Port ni dans un « bureau fermé ».
- [ ] Le magistrat est sous les Archives, et l'ascenseur se trouve juste à côté.
- [ ] Aucun protagoniste ne mentionne spontanément la Garde des Profondeurs avant de la connaître.
- [ ] Les Sentinelles du Cœur apparaissent comme une présence cohérente aux accès sensibles du Port.
- [ ] Léovic demeure cordial, expérimenté et professionnel, **sans grade inventé**.
- [ ] Les renvois vers les menus fonctionnent sans répétitions inutiles.
- [ ] Les 50 Martins sont versés systématiquement pendant F06 ; l’embarras est neutre et la réponse sèche D coûte −1 sans annuler le don.

**Implémentation et tests**

- [ ] Les IDs éditoriaux sont mappés aux fonctions existantes, sans remplacement arbitraire des IDs de quête.
- [ ] L'instance privée de Léovic ne gêne aucun autre joueur ; le Léovic public demeure à son poste.
- [ ] Le pathfinding s'arrête dès une interaction facultative et reste arrêté jusqu'à « Reprenons ».
- [ ] La caméra revient au joueur même en cas de sortie imprévue.
- [ ] Le magistrat et Léovic peuvent dialoguer sans déplacement parasite.
- [ ] `CAP_RANGSOCIAL=10` à l'inscription seulement si rang inférieur, sans régression.
- [ ] `REP_GARDEPORT` : **aucun +5 final** ; jusqu’à **trois +1** par décisions chaleureuses, **−1** si mépris exprimé au don.
- [ ] Les 50 Martins ne sont jamais versés deux fois, y compris après fermeture, reconnexion ou reprise d’une ancienne sauvegarde en cours ; aucun menu de récupération au retour.
- [ ] Les quatre décisions, l’impression globale et la réputation sont séparées entre deux personnages Nexus du même compte.
- [ ] Les étapes et la scène se récupèrent correctement après déconnexion.

## Décisions encore ouvertes, limitées

1. **Nom du magistrat :** facultatif, non fixé.
2. **Grade exact de Léovic :** volontairement non fixé ; n'empêche pas la mise en scène.
3. **Don : décision utilisateur du 01/11/2026.** Versement systématique pendant la scène, réponses A/B neutres, C +1/+1, D −1/−1 ; aucun refus/report ni retour pour obtenir l’argent.
4. **Placement des commentaires de marche et des Sentinelles :** à caler sur les positions réellement présentes dans la carte et le core à jour.
5. **État technique actuel :** vérifier les systèmes de caméra, de copie PNJ et de persistance Nexus sur le build qui sera effectivement déployé.

---

**Fin — PQ02 v1.1 : source v1.0 conservée, adaptation limitée au versement systématique demandé le 01/11/2026.**
