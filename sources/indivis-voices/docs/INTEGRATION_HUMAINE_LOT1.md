# Intégration des prises humaines — lot 1

Version **0.2.0+1.21.11**, banque **prologue-fr-arthur-1**, 19 octobre 2026.

Les 18 prises humaines de Géraud et Althéon sont incluses dans le JAR. Les deux
répliques Léovic du prototype restent synthétiques. Aucun pack externe à activer.
Les identifiants runtime des huit répliques existantes sont conservés.

## Correspondances livrées

Les codes viennent du PDF fourni. Le texte ci-dessous est celui du core 1.5.7 ;
les correspondances ont été vérifiées par égalité exacte, sans recherche
approximative. Les fichiers OGG préparés sont copiés octet pour octet. Le fichier
[`human-takes.json`](../human-takes.json), également embarqué dans le JAR, relie
chaque code à l'asset, au WAV original, à ses bornes de coupe en échantillons,
aux empreintes OGG/FLAC, au texte et à la fonction native.

| Code du PDF | ID runtime | Durée (s) | CAP_QSEQ suivant | Texte natif |
| --- | --- | ---: | --- | --- |
| `P01-GER-004` | `prologue.geraud.reveil.01` | 6.689 | 101 | Vous voilà sur vos jambes. Lorsque nous vous avons recueilli, j’aurais parié moins volontiers sur votre réveil. |
| `P01-GER-010` | `prologue.geraud.reveil.02` | 7.348 | 102 | Nous approchons de la Haute Capitale. Le capitaine Brumeforge souhaite vous parler avant que le bâtiment ne prenne le quai. |
| `P01-GER-011` | `prologue.geraud.reveil.03` | 10.178 | 103 | Sa cabine se trouve sous le poste du timonier, au gaillard d’arrière. Présentez-vous à lui directement ; sur ce pont, une convocation ne se transmet pas de loin. |
| `P01-GER-005` | `prologue.geraud.rappel.01` | 6.037 | 0 (autonome) | Le capitaine Brumeforge vous attend dans sa cabine. Présentez-vous à lui avant l’accostage. |
| `P01-ALT-001` | `prologue.altheon.invitation.01` | 9.183 | 0 (autonome) | Entrez donc. Le quartier-maître a dû vous prévenir : j’aimerais vous parler avant l’accostage. Adressez-vous à moi lorsque vous serez prêt. |
| `P01-ALT-002` | `prologue.altheon.invitation.02` | 8.451 | 0 (autonome) | Entrez, je vous prie. Avant que nous touchions les quais, il nous faut échanger quelques mots. Venez me parler lorsque vous serez prêt. |
| `P01-ALT-003` | `prologue.altheon.invitation.03` | 10.497 | 0 (autonome) | Nous approchons de la Haute Capitale. Avant l’accostage, je veux entendre ce que vous pouvez dire de votre situation. Parlez-moi dès que vous êtes disposé. |
| `P01-ALT-004` | `prologue.altheon.intro.01` | 7.940 | 201 | Nous revenions vers la Haute Capitale lorsque la vigie vous a aperçu, seul, loin des routes que nous reconnaissons. |
| `P01-ALT-005` | `prologue.altheon.intro.02` | 8.493 | 202 | Vous n’aviez ni escorte, ni insigne, ni nom que nous puissions saisir sur le moment. Le plus juste était de vous prendre à bord. |
| `P01-ALT-006` | `prologue.altheon.intro.03` | 8.426 | 203 | Nous toucherons bientôt les quais. Posez vos questions à présent : je vous dirai ce que je sais, sans inventer ce qui m’échappe. |
| `P01-ALT-010` | `prologue.altheon.questions.decouverte.01` | 10.219 | 211 | Sur un relief battu par les vents, loin des mouillages et des routes marquées sur nos cartes. Vous respiriez encore, mais pas assez fort pour nous raconter votre histoire. |
| `P01-ALT-023` | `prologue.altheon.questions.decouverte.02` | 5.726 | 212 | Je ne vous prêterai donc ni passé ni intention. Ce récit vous appartient. |
| `P01-ALT-011` | `prologue.altheon.questions.mission.01` | 12.563 | 221 | Nous avions ordre de livrer plusieurs canons à Avaleiv. La cargaison déposée, nous avons repris le cap de la Haute Capitale. C'est sur le trajet du retour que nous vous avons découvert. |
| `P01-ALT-024` | `prologue.altheon.questions.mission.02` | 7.222 | 222 | Sur un bâtiment de la Couronne, les raisons ne descendent pas toujours jusqu’aux mains qui tiennent le gouvernail. |
| `P01-ALT-008` | `prologue.altheon.questions.avaleiv.01` | 12.705 | 251 | Une principauté elfique indépendante de la Couronne, où se côtoient marchands et voyageurs de différents peuples. Nous y entretenons des relations commerciales, mais ce n'est pas une terre impériale. |
| `P01-ALT-009` | `prologue.altheon.questions.capitale.01` | 13.798 | 231 | La Haute Capitale est le centre de la Couronne : ses quais accueillent, ses bureaux enregistrent, ses hauteurs décident. Un nouvel arrivant y rencontre souvent un registre avant de trouver sa première rue. |
| `P01-ALT-022` | `prologue.altheon.questions.capitale.02` | 9.989 | 232 | Le Port paraît d’abord pratique. Regardez mieux : les Archives le dominent, les contrôles l’ordonnent, et l’on finit toujours par entendre parler du Cœur. |
| `P01-ALT-007` | `prologue.altheon.questions.amnesie.01` | 12.600 | 241 | J'aimerais pouvoir vous répondre, mais nous vous avons trouvé seul et sans connaissance. Personne à bord ne vous connaissait. Quant à ce qui vous est arrivé avant que notre vigie vous aperçoive, je n'en sais rien. |

`P01-GER-005` utilise **take01** ; les trois autres interprétations sont conservées
dans l'archive de préparation. Aucune sélection aléatoire ne peut remplacer la
prise choisie. Pour 01.05, la source utilisée est le WAV corrigé
`Doublage Althéon Brumeforge 01.05(1).wav`, pas le premier fichier incomplet.

## Sources et conservation

* Core 1.5.7 fourni : SHA-256 `9b9c20bbc0a28d85384d7bb5e7d00199fee87af795fc081a24e3f0971c61e9df`.
* Archive des 21 prises préparées : `indivis_voices_prises_humaines_lot1_2026-10-19.zip`,
  SHA-256 `b8541d579e46223d709629aa2257561acb768fdb5cad709e90f64acc63126271`.
* Les masters humains FLAC restent dans cette archive, avec les variantes et
  le manifeste de coupe. Les WAV originaux sont conservés inchangés. Aucun
  master sans pertes n'est embarqué dans le mod.
* Les six masters synthétiques remplacés sont archivés dans
  `demo-masters/original-synthetic/`. Seuls les deux masters Léovic encore
  utilisés restent à la racine de `demo-masters/`.

## Déclenchement et progression

Les huit réponses aux questions du capitaine utilisent les séquences natives
211/212, 221/222, 251, 231/232 et 241. Le core conserve ses choix, ses conditions,
ses marqueurs de connaissance et son dispatch ; Voices ne lance aucune fonction
de quête. Le timer des lignes séquencées reste géré par le bail court existant.

Les trois invitations d'Althéon et le rappel de Géraud sont des répliques
isolées, avec CAP_QSEQ à 0. Voices exige une session b10 réelle, le bon PNJ et le
personnage Nexus actif. Il n'écrit aucun timer pour ces quatre lignes, quelle
que soit la fin audio. Si le joueur choisit de continuer et change la séquence,
la voix précédente est annulée. Les boutons restent ceux du dialogue natif.

Le client laisse au plus une seconde à b10 pour ouvrir son écran et capturer
le texte exact de la réplique demandée. Une absence ou une différence conserve
le texte natif ; aucune autre prise n'est choisie à sa place. Le protocole v1,
les caméras et les interfaces vérifiées de b10 restent conservés.

## Vérifications et essai en jeu

Compilation propre Java 21, 22 tests JUnit réussis, catalogue confronté au ZIP
exact du core, vérification des signatures du JAR b10 fourni, et contrôle du
JAR remappé : 20 OGG, empreintes, événements sonores et provenance. Les contrôles
négatifs refusent notamment l'échange d'audio entre deux codes. Les résultats
et les empreintes livrées sont dans [verification-0.2.0.json](verification-0.2.0.json).

Sur le serveur **de test** et le client, remplacer uniquement l'ancien Voices
par `indivis-voices-0.2.0+1.21.11.jar`, puis redémarrer les deux. `/ivvoices` doit
indiquer 20/20 prises vérifiées. Hors dialogue, `/ivvoices test P01-ALT-010`
joue la prise demandée et affiche son texte ; tout code de la table est accepté.
Le test local ne déclenche aucune progression de quête.

La recette en jeu reste à effectuer : écouter les huit réponses et leurs
branches, cliquer pendant une invitation, passer/interrompre une voix, changer
de personnage, vérifier les caméras et tester deux joueurs. La compilation ne
valide ni l'écoute effective sur le monde du serveur ni les performances FPS/MSPT.
Aucun déploiement en production n'a été effectué.
