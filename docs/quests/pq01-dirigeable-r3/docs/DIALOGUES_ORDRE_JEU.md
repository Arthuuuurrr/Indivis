# Dialogues PQ01 — ordre technique et choix

Texte du prompt validé ; les contrôles Continuer/Fermer sont techniques. A2 → trajet aller → A3 ; A6 → trajet retour → A7.

## a1 — Géraud Rivet

> Ah ! Vous voilà enfin sur vos jambes. Doucement, tout de même. Vous nous avez fait une sacrée frayeur.

- Où suis-je ? → `a2`

## a2 — Géraud Rivet

> À bord d'un dirigeable de la Couronne. On vous a récupéré sur un rocher, inconscient. Quant à savoir comment vous avez atterri là... ça, c'est une autre histoire.

- Continuer. → `a3`

## a3 — Gautier

> Quartier-maître ! Le capitaine a demandé que notre passager lui soit présenté avant l'arrivée. Nous devrions peut-être...

- Continuer. → `a4`

## a4 — Géraud Rivet

> Matelot !

- Continuer. → `a5`

## a5 — Géraud Rivet

> Je connais les ordres du capitaine. Retournez à votre poste et laissez-moi faire mon travail.

- Continuer. → `a6`

## a6 — Gautier

> Bien, quartier-maître !

- Continuer. → `a7`

## a7 — Géraud Rivet

> Enfin... pour une fois qu'il prend une initiative, il aurait pu choisir un meilleur moment.

- Le capitaine veut me voir ? → `a8`

## a8 — Géraud Rivet

> En effet. Mais vous venez à peine de vous réveiller. Prenez une minute pour retrouver vos esprits, puis allez le rejoindre. Sa cabine est à l'arrière, sous le poste du timonier.

- Continuer. → `ga_menu`

## ga_menu — Géraud Rivet

> Vous avez besoin d'autre chose ? Faites vite, j'ai encore du travail avant notre arrivée.

- Vous étiez là quand on m'a retrouvé ? → `ga_12`
- Quel est votre rôle sur ce dirigeable ? → `ga_14`
- Vous connaissez le capitaine depuis longtemps ? → `ga_16`
- Le matelot avait pourtant raison, non ? → `ga_18`
- Je vais rejoindre le capitaine. → `ga_20`

## ga_12 — Géraud Rivet

> Oui. Pas tous les jours qu'on interrompt un retour de mission pour récupérer quelqu'un sur un rocher. Vous ne répondiez à rien. On vous a installé à bord et on a fait ce qu'on pouvait.

- Continuer. → `ga_menu`

## ga_14 — Géraud Rivet

> Quartier-maître. Je m'assure que le matériel soit à sa place, que les hommes fassent leur travail et que le capitaine n'ait pas à régler chaque petit problème lui-même. Croyez-moi, ça en fait déjà suffisamment.

- Continuer. → `ga_menu`

## ga_16 — Géraud Rivet

> Assez pour savoir quand il vaut mieux le laisser travailler. C'est un bon officier. Exigeant, oui, mais il n'envoie pas ses hommes faire ce qu'il refuserait lui-même.

- Continuer. → `ga_menu`

## ga_18 — Géraud Rivet

> Sur les ordres ? Oui. Sur le moment de venir me les rappeler ? Beaucoup moins. Il apprendra.

- Continuer. → `ga_menu`

## ga_20 — Géraud Rivet

> Bonne idée. Et ne vous sentez pas obligé de vous mettre au garde-à-vous : vous êtes notre passager, pas une nouvelle recrue.

- Continuer. → `close`

## gautier_menu — Gautier

> Ah, vous voilà. Je crains de ne pas avoir choisi le meilleur moment pour intervenir tout à l'heure.

- Géraud avait l'air furieux. → `gautier_23`
- Vous avez peur de lui ? → `gautier_25`
- Vous travaillez sur ce dirigeable depuis longtemps ? → `gautier_27`
- Vous tenez beaucoup à votre carrière. → `gautier_29`
- Où puis-je trouver le capitaine ? → `gautier_31`
- Je vous laisse reprendre votre service. → `gautier_33`

## gautier_23 — Gautier

> Furieux ? Non, je ne crois pas. Enfin... il a connu de meilleurs moments pour me voir. Je pensais simplement respecter les consignes.

- Continuer. → `gautier_menu`

## gautier_25 — Gautier

> Peur ? Certainement pas ! C'est un excellent quartier-maître. Mais je préférerais éviter qu'il ait à me reprendre une deuxième fois aujourd'hui.

- Continuer. → `gautier_menu`

## gautier_27 — Gautier

> Suffisamment pour connaître mon service. J'espère d'ailleurs obtenir davantage de responsabilités. J'ai déjà pris certaines dispositions pour... Enfin, cela ne vous intéresse probablement pas.

- Continuer. → `gautier_menu`

## gautier_29 — Gautier

> Évidemment. On n'obtient pas un avancement en attendant que les choses arrivent toutes seules. Encore faut-il se faire remarquer pour les bonnes raisons.

- Continuer. → `gautier_menu`

## gautier_31 — Gautier

> À l'arrière, sous le poste du timonier. Présentez-vous directement à sa cabine. Vous ne pouvez pas vous tromper.

- Continuer. → `gautier_menu`

## gautier_33 — Gautier

> Merci. Et... j'espère que vous vous remettrez rapidement.

- Continuer. → `close`

## hugues_menu — Hugues

> Trois fois qu'on me fait recompter les mêmes caisses... Ah, pardon. Vous cherchiez quelque chose ?

- Qu'est-ce que vous transportiez ? → `hugues_36`
- Pourquoi avoir livré des canons à Avaleiv ? → `hugues_38`
- Vous n'aimez pas votre travail ? → `hugues_40`
- Vous connaissiez déjà Avaleiv ? → `hugues_42`
- Vous étiez là lorsqu'on m'a secouru ? → `hugues_44`
- Écouter leur échange sur les procédures. → `banter_start`
- Je vous laisse à vos caisses. → `hugues_46`

## hugues_36 — Hugues

> Des canons. Enfin, nous les avons livrés à Avaleiv. C'est déjà ça de moins à surveiller. Vous auriez vu le temps qu'il nous a fallu pour les faire décharger...

- Continuer. → `hugues_menu`

## hugues_38 — Hugues

> Parce qu'on nous en a donné l'ordre. Quant à savoir ce qui s'est négocié là-bas, il faudrait demander à ceux qui signent les papiers. Moi, je m'occupe de ce qu'ils nous font transporter.

- Continuer. → `hugues_menu`

## hugues_40 — Hugues

> Si je le détestais vraiment, je ne serais plus là. Et puis, il y a pire comme équipage. Ne répétez pas ça à Géraud, il finirait par se croire indispensable.

- Continuer. → `hugues_menu`

## hugues_42 — Hugues

> J'y ai accosté plus d'une fois. C'est un beau pays, du peu que j'en ai vu. Mais entre les cargaisons et les ordres de départ, on ne nous laisse pas vraiment le temps de jouer les touristes.

- Continuer. → `hugues_menu`

## hugues_44 — Hugues

> Oui. J'ai vu qu'on vous remontait à bord. Vous ne donniez pas franchement l'impression d'avoir passé une bonne journée. Enfin, regardez-vous : vous tenez debout et vous posez des questions. C'est encourageant.

- Continuer. → `hugues_menu`

## hugues_46 — Hugues

> Ah, ne m'en parlez plus, de ces fichues caisses ! Bonne continuation.

- Continuer. → `close`

## anselme_menu — Anselme

> Un instant... Un instant... Voilà. Excusez-moi. Vous vouliez quelque chose ?

- C'est difficile de piloter un dirigeable ? → `anselme_49`
- Vous semblez aimer votre métier. → `anselme_51`
- Vous avez aperçu l'endroit où l'on m'a retrouvé ? → `anselme_53`
- Comment fonctionne ce dirigeable ? → `anselme_55`
- Votre appareil semble compliqué. → `anselme_57`
- Je vais vous laisser piloter. → `anselme_59`

## anselme_49 — Anselme

> Difficile ? Tout dépend de ce que vous appelez piloter. Maintenir un cap par temps clair, n'importe quel apprenti convenablement formé peut y parvenir. Mais lorsque les vents changent et que le bâtiment commence à dériver... Ah ! Là, vous voyez ce que vaut réellement un timonier.

- Continuer. → `anselme_menu`

## anselme_51 — Anselme

> Aimer ? C'est bien peu dire. Il n'y a rien de comparable à la sensation d'un bâtiment qui répond exactement à vos commandes. Enfin... lorsque la mécanique ne décide pas de vous contrarier.

- Continuer. → `anselme_menu`

## anselme_53 — Anselme

> Pas assez pour vous en donner une position exacte. Nous avons quitté notre trajectoire habituelle pour procéder au sauvetage, mais j'avais autre chose à surveiller que le paysage. Le capitaine pourra vous en dire davantage.

- Continuer. → `anselme_menu`

## anselme_55 — Anselme

> Vous voulez une explication courte ou une explication exacte ? Parce que ce n'est pas tout à fait la même chose. Pour simplifier : il faut contrôler la sustentation, la propulsion et la direction. Et aucun de ces trois éléments n'a la politesse de rester parfaitement stable.

- Continuer. → `anselme_menu`

## anselme_57 — Anselme

> Notre bâtiment, si vous permettez. Et il est complexe, oui, ce qui n'est pas exactement la même chose. Enfin, je suppose que vous ne pouviez pas le savoir.

- Continuer. → `anselme_menu`

## anselme_59 — Anselme

> Sage décision. Nous approchons et je préfère avoir les yeux sur mes instruments. Bonne visite à bord.

- Continuer. → `close`

## perrin_menu — Perrin

> Ah, c'est donc vous, notre fameux rescapé ! Excusez-moi, mais depuis votre arrivée, vous avez suscité quelques conversations à bord. Comment vous sentez-vous ?

- Vous parlez souvent de moi ? → `perrin_62`
- Vous savez ce qui m'est arrivé ? → `perrin_64`
- Vous connaissez bien la Haute Capitale ? → `perrin_66`
- Comment sont les habitants de la Capitale ? → `perrin_68`
- On m'a parlé d'un enregistrement à mon arrivée. → `perrin_70`
- Je préfère continuer ma visite. → `perrin_72`

## perrin_62 — Perrin

> Oh, pas constamment ! Mais avouez que ce n'est pas tous les jours qu'on recueille quelqu'un dans de pareilles circonstances. Certains se demandaient même si vous alliez vous réveiller avant notre arrivée.

- Continuer. → `perrin_menu`

## perrin_64 — Perrin

> J'aimerais bien ! J'aurais une sacrée histoire à raconter. Mais non, malheureusement. À part ce qu'on vous a probablement déjà dit, je n'en sais pas plus.

- Continuer. → `perrin_menu`

## perrin_66 — Perrin

> Plutôt, oui ! Les quais, surtout. Si vous aimez les endroits tranquilles, ce n'est peut-être pas le meilleur quartier pour commencer. Mais pour rencontrer du monde, vous serez servi.

- Continuer. → `perrin_menu`

## perrin_68 — Perrin

> Il y a de tout. Des marchands qui vous vendraient leur propre chapeau, des soldats qui prennent leur uniforme très au sérieux et des gens parfaitement charmants. Vous finirez bien par vous y faire quelques connaissances.

- Continuer. → `perrin_menu`

## perrin_70 — Perrin

> Ah oui, les registres ! On n'y échappe pas facilement. Mais ne vous inquiétez pas, on vous expliquera comment procéder. Ce serait tout de même malheureux de vous perdre dans les bureaux après vous avoir retrouvé en pleine nature.

- Continuer. → `perrin_menu`

## perrin_72 — Perrin

> Faites donc ! Et bonne chance avec le capitaine. Il a l'air sévère au premier abord, mais c'est un brave homme. Enfin, ne lui répétez pas que je l'ai dit comme ça.

- Continuer. → `close`

## b0 — Gautier

> Vous avez terminé le contrôle du matériel ?

- Continuer. → `b1`

## b1 — Hugues

> Oui.

- Continuer. → `b2`

## b2 — Gautier

> Et vous avez signé le registre ?

- Continuer. → `b3`

## b3 — Hugues

> Pas encore.

- Continuer. → `b4`

## b4 — Gautier

> Alors le contrôle n'est pas terminé.

- Continuer. → `b5`

## b5 — Hugues

> Vous savez, un jour, vous découvrirez qu'une caisse ne disparaît pas simplement parce qu'on a oublié de signer un papier.

- Continuer. → `b6`

## b6 — Gautier

> Ce n'est pas une raison pour négliger les procédures.

- Continuer. → `b7`

## b7 — Hugues

> Et voilà. Je savais que j'aurais dû me contenter de vous dire bonjour.

- Continuer. → `hugues_menu`

## c0 — Althéon Brumeforge


- Je vous écoute. → `c1`

## c1 — Althéon Brumeforge

> Entrez, je vous prie. Vous êtes donc notre rescapé. Je suis Althéon Brumeforge, capitaine de ce bâtiment.

- Continuer. → `c2`

## c2 — Althéon Brumeforge

> Je suis heureux de vous voir remis. Lorsque nous vous avons trouvé, votre état était préoccupant.

- Vous savez ce qui m'est arrivé ? → `c3`

## c3 — Althéon Brumeforge

> C'est précisément ce que j'espérais vous demander.

- Continuer. → `c4`

## c4 — Althéon Brumeforge

> Vous souvenez-vous de ce qui vous a conduit là-bas ?

- Non. Je ne me souviens de rien avant votre sauvetage. → `c5`

## c5 — Althéon Brumeforge

> Je vois...

- Continuer. → `c6`

## c6 — Althéon Brumeforge

> Dans ce cas, je crains de ne pouvoir vous donner toutes les réponses que vous espérez. Mais je peux au moins vous expliquer ce que nous savons.

- Continuer. → `c_menu`

## c_menu — Althéon Brumeforge


- Où m'avez-vous trouvé ? → `ca1`
- Quelle était votre mission ? → `cb1`
- Qu'est-ce qu'Avaleiv ? → `cc1` (après la mission d’Althéon)
- Que devrais-je savoir de la Haute Capitale ? → `cd1`
- Pensez-vous que je puisse retrouver la mémoire ? → `ce1`
- Merci, capitaine. J'en sais assez pour le moment. → `cx1`

## ca1 — Althéon Brumeforge

> Notre vigie vous a aperçu sur un relief rocheux, exposé aux vents, à l'écart de notre trajectoire. Vous étiez seul et ne sembliez pas en état de nous appeler à l'aide.

- Vous avez pu déterminer de quel endroit il s'agissait ? → `ca2`
- Revenir aux questions. → `c_menu`

## ca2 — Althéon Brumeforge

> Pas avec certitude. Nous avons interrompu notre route pour vous porter secours, sans entreprendre de reconnaissance des environs.

- Pourquoi ne pas avoir examiné les lieux ? → `ca3`
- Revenir aux questions. → `c_menu`

## ca3 — Althéon Brumeforge

> Vous étiez inconscient. Ma priorité était de vous mettre en sécurité, pas de satisfaire ma curiosité.

- Continuer. → `c_menu`

## cb1 — Althéon Brumeforge

> Nous avions pour ordre de livrer plusieurs pièces d'artillerie à Avaleiv. La cargaison remise à ses destinataires, nous avons repris la route de la Haute Capitale. C'est au retour que nous vous avons découvert.

- Pourquoi l'Empire livre-t-il des canons à Avaleiv ? → `cb2`
- Revenir aux questions. → `c_menu`

## cb2 — Althéon Brumeforge

> Les relations entre États ne se limitent pas au commerce des étoffes et des denrées. Certaines livraisons relèvent d'accords dont je ne négocie pas les termes. Mon devoir était d'assurer le transport.

- Vous ne connaissez pas les raisons de cette livraison ? → `cb3`
- Revenir aux questions. → `c_menu`

## cb3 — Althéon Brumeforge

> J'en connais la destination et le caractère officiel. Pour le reste, je préfère ne pas vous présenter des suppositions comme des certitudes.

- Continuer. → `c_menu`

## cc1 — Althéon Brumeforge

> Une principauté elfique indépendante. Elle entretient des relations commerciales avec l'Empire, mais ne relève pas de l'autorité de la Couronne.

- Vous y allez souvent ? → `cc2`
- Revenir aux questions. → `c_menu`

## cc2 — Althéon Brumeforge

> Suffisamment pour connaître certaines routes et les usages des ports que nous fréquentons. Mais je n'aurais pas la prétention de connaître la principauté dans son ensemble.

- Continuer. → `c_menu`

## cd1 — Althéon Brumeforge

> C'est le centre de l'Empire et le siège de la Couronne. Vous y trouverez de quoi vous loger, travailler et, je l'espère, obtenir de l'aide pour vos recherches.

- Continuer. → `cd2`

## cd2 — Althéon Brumeforge

> Mais vous devrez d'abord faire enregistrer votre arrivée. C'est une formalité à laquelle les autorités accordent beaucoup d'importance.

- Et si je ne connais pas mon passé ? → `cd3`
- La Capitale est-elle accueillante envers les étrangers ? → `cd4`
- Revenir aux questions. → `c_menu`

## cd3 — Althéon Brumeforge

> Dites-le. Vous avez été recueilli par un bâtiment impérial, et notre rapport en attestera. Je ne vois aucune raison de vous demander d'inventer ce que vous ignorez.

- La Capitale est-elle accueillante envers les étrangers ? → `cd4`
- Revenir aux questions. → `c_menu`

## cd4 — Althéon Brumeforge

> C'est une ville où se croisent de nombreux peuples. Vous y rencontrerez des gens de toutes dispositions. Je ne vous promettrai pas qu'ils seront tous aimables, mais vous n'avez pas à vous présenter comme un intrus pour autant.

- Continuer. → `c_menu`

## ce1 — Althéon Brumeforge

> Je ne suis pas médecin, et je me garderais bien de vous en faire la promesse.

- Continuer. → `ce2`

## ce2 — Althéon Brumeforge

> Mais ne concluez pas trop vite que votre passé est définitivement perdu. Vous avez peut-être laissé des traces quelque part, même si vous ne vous en souvenez pas.

- Vous croyez vraiment que je pourrai découvrir quelque chose ? → `ce3`
- Revenir aux questions. → `c_menu`

## ce3 — Althéon Brumeforge

> Je crois surtout qu'il serait prématuré de renoncer.

- Continuer. → `c_menu`

## cx1 — Althéon Brumeforge

> Très bien. Nous approchons des quais. Vous pouvez rester ici pendant la manœuvre ; Géraud vous retrouvera une fois l'amarrage terminé.

- Continuer. → `cx2`

## cx2 — Althéon Brumeforge

> Et une dernière chose...

- Continuer. → `cx3`

## cx3 — Althéon Brumeforge

> Ne vous reprochez pas de ne pas avoir de réponses aujourd'hui. Nous ignorons encore bien des choses sur les circonstances de votre découverte.

- Continuer. → `cx4`

## cx4 — Althéon Brumeforge

> Pour l'instant, tâchez simplement de reprendre pied. Nous verrons ensuite ce que l'avenir vous réserve.

- Continuer. → `dock`

## e1 — Géraud Rivet

> Ah, vous voilà ! Bienvenue dans la Haute Capitale. Enfin... sur ses quais, pour commencer.

- Continuer. → `e2`

## e2 — Géraud Rivet

> Vous pouvez descendre. Mais avant de vous laisser partir, il y a deux ou trois choses que vous feriez bien de savoir.

- Continuer. → `e_menu`

## e_menu — Géraud Rivet


- Que dois-je faire maintenant ? → `e_127`
- Je risque d'avoir des ennuis avec les gardes ? → `e_129`
- Je peux circuler librement dans la Capitale ? → `e_131`
- Vous allez repartir bientôt ? → `e_133`
- Merci de m'avoir aidé. → `e_135`
- Je vais y aller. → `ex1`

## e_127 — Géraud Rivet

> Commencez par vous faire enregistrer. Cherchez Léovic sur les quais, il saura vous indiquer la marche à suivre. Après ça, vous pourrez vous occuper de trouver un toit et de découvrir la ville.

- Continuer. → `e_menu`

## e_129 — Géraud Rivet

> Tant que vous ne leur donnez pas de raison d'en chercher, ça devrait aller. Ils pourront vous demander d'où vous venez. Dites que vous avez été recueilli par le capitaine Brumeforge.

- Continuer. → `e_menu`

## e_131 — Géraud Rivet

> Dans les quartiers ouverts au public, oui. Pour le reste, il y a des contrôles. Et croyez-moi, certains gardes prennent leur travail aussi à cœur que notre jeune matelot.

- Continuer. → `e_menu`

## e_133 — Géraud Rivet

> Pas immédiatement. Il faut vérifier le bâtiment, régler les formalités de retour et préparer les prochaines affectations. On ne termine pas un voyage simplement parce qu'on a posé les amarres.

- Continuer. → `e_menu`

## e_135 — Géraud Rivet

> Bah, on n'allait tout de même pas vous laisser sur votre rocher. Et maintenant que vous tenez debout, tâchez de le rester.

- Continuer. → `e_menu`

## ex1 — Géraud Rivet

> Bon. Je crois que je vous ai dit l'essentiel.

- Continuer. → `ex2`

## ex2 — Géraud Rivet

> Allez trouver Léovic, et ne vous laissez pas impressionner par toute cette agitation. On finit par s'y habituer.

- Continuer. → `ex3`

## ex3 — Géraud Rivet

> Prenez soin de vous, surtout. J'aimerais autant que la prochaine fois qu'on se croise, vous soyez en meilleure forme que lors de notre première rencontre.

- Je ferai de mon mieux. → `ex4`

## ex4 — Géraud Rivet

> Voilà qui me suffit. Allez, filez. J'ai un équipage à remettre au travail, moi.

- Continuer. → `complete`

## f_altheon — Althéon Brumeforge

> Vous avez pu parler à Géraud ? Bien. J'espère que vous trouverez vos repères dans la Capitale. Quant à moi, j'ai encore quelques rapports à terminer.

- Continuer. → `close`

## f_gautier — Gautier

> La passerelle est en place et les opérations de débarquement sont autorisées. Vous pouvez rejoindre les quais. Bonne continuation !

- Continuer. → `close`

## f_hugues — Hugues

> À peine rentrés et voilà déjà les inventaires qui recommencent. Ah, vous partez ? Profitez-en. Vous, au moins, personne ne vous réclamera un relevé des soutes.

- Continuer. → `close`

## f_anselme — Anselme

> Un amarrage parfaitement convenable, malgré ce vent de travers. J'aurais préféré une approche un peu plus... Enfin, vous n'avez pas à vous soucier de ces détails. Bonne continuation.

- Continuer. → `close`

## f_perrin — Perrin

> Alors, prêt à découvrir la Capitale ? Ne passez pas toute votre première journée dans les bureaux, tout de même. Il y a bien des choses à voir ici !

- Continuer. → `close`

## f_geraud — Géraud Rivet

> La passerelle reste libre. Le Port est devant vous ; bonne route à terre.

- Continuer. → `close`

