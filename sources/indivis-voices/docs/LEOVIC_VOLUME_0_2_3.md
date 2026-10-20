# Léovic et volume — 0.2.3

20 octobre 2026. Issue #167, PR #171. Branche de développement uniquement,
sans déploiement ni modification de main. Dépendances identiques à 0.2.2.

## Ce que montrent les deux nouveaux logs

Le serveur et le client chargent Voices 0.2.2, dialogue b10 et 20 prises.
Le client confirme `lecture ×3.0 (limiteur de sortie actif, fichiers intacts)`.
Les trois prises de Géraud démarrent successivement ; la correction de cascade
est active sur le serveur.

À 07:03:35 côté client, `prologue.leovic.inscription.01` démarre. À 07:03:41,
une `SocketException: Connection reset` interrompt la connexion, avant les
8 292 ms de la prise. Le serveur constate ensuite un timeout. Après
reconnexion, une nouvelle session Léovic s'ouvre et le choix QuestChoix 11
est envoyé. Les anciens logs n'indiquent ni le motif de fin vocal ni les
scores après libération : ils ne prouvent pas une attente infinie de Voices,
ni la cause de la coupure réseau.

Le core 1.5.7 fourni à nouveau est identique octet pour octet à celui audité :
SHA-256 `9b9c20bbc0a28d85384d7bb5e7d00199fee87af795fc081a24e3f0971c61e9df`.
Le JAR b10 a toujours l'empreinte
`a2c21f24889f96880c472d168ea55cf65e84ff154ee1bbcc22dbeaa9df1d4057`.

## Transition d'affichage corrigée

Le vrai b10 ajoute les répliques capturées à `REPLIQUES`. Son panneau conserve
la page courante lorsque `pageRemise` est faux, et affiche les choix uniquement
sur la dernière page. La reprise native de Léovic après la voix 502 ajoute
« Léovic vous adresse une proposition. » puis les trois choix. L'ancien
paragraphe parlé peut donc rester affiché, les choix sur une page suivante.
Ce défaut d'affichage est établi par les sources ; il n'est pas une preuve
que toute l'observation utilisateur découle de lui malgré la coupure.

Après une fin naturelle ou un passage volontaire d'une voix séquencée,
le client attend au plus cinq secondes la suite native. S'il retrouve
exactement l'objet texte parlé en tête, dans la même session et avec le même
PNJ, il retire uniquement cet ancien paragraphe quand une suite existe et
réinitialise l'affichage à sa première page. Les nouveaux textes sont gardés
intégralement. Les choix, l'historique b10, le typewriter, les caméras et les
commandes du joueur restent natifs. Une réponse déjà remplacée par un choix
ne déclenche pas cette opération, même si son texte ressemble à la phrase
précédente. Pendant la lecture de l'historique, aucune page n'est déplacée.

Un échec audio ou une fermeture conserve le retour textuel existant. Aucun
nouveau texte ou son n'est inventé. Le catalogue compte toujours deux prises
synthétiques de Léovic : sa proposition et son accompagnement. Ses autres
phrases restent textuelles.

Les nouveaux logs montrent le début, le motif de fin, le temps écoulé,
la libération du timer et la séquence observée ensuite. Les fins prématurées
sont signalées au plus une fois par lecture. Une observation tardive ne
réécrit jamais la progression et est abandonnée au changement de personnage.

## Volume

Le niveau natif de lecture passe de ×3 à ×6, soit un doublement supplémentaire
sur les passages qui restent sous le limiteur. Les fichiers OGG, les masters,
les SHA-256 et les durées sont inchangés. Le même contrôle de capacité OpenAL
et du limiteur de sortie est conservé. Aucune autre source sonore n'est
amplifiée ; le plafond de la source Voices est rétabli à son arrêt.

Le banc avec la bibliothèque Linux de Minecraft donne un rapport RMS de
5,99999993 au volume initial, et de 2,0 à la 0.2.2, sur son signal faible.
Le signal fort reste dans la plage numérique, crête 0,99999994. Ce contrôle
ne mesure pas le niveau acoustique du casque et ne garantit pas une absence
de distorsion déjà présente dans l'enregistrement ou le matériel.

`/ivvoices volume 50` retrouve le niveau de la 0.2.2 ; le réglage à 100 utilise
×6 sur un moteur compatible. `/ivvoices` affiche le profil réellement activé.

## Vérifications et essai restant

Six tests de transition couvrent la suite non doublée, plusieurs nouveaux
paragraphes, les choix sans nouveau texte, le remplacement après choix,
l'historique et les changements de contexte/délai. Un test du bail Léovic
vérifie les fins naturelle/passée, les échecs et l'expiration bornée.

Le banc Fabric reprend 14 fonctions exactes du core en fixtures de test,
vérifiées par empreinte et comparées au ZIP fourni. Douze contrôles suivent
le menu après voix, l'absence de répétition, la question facultative et son
retour au menu, l'acceptation, le démarrage unique de l'escorte, le refus,
un guide déjà occupé et deux sources indépendantes. Les trois entrées de
branche ont uniquement un compteur de test ajouté. Les services privés et
du monde sont remplacés par des compteurs ; les sources sont des markers.
Ce banc ne simule ni un vrai joueur réseau, ni le rendu b10, ni la navigation
EasyNPC. Les 42 anciens contrôles du dispatcher sont aussi conservés.

Installer le JAR 0.2.3 côté client et serveur de test pour les nouvelles traces.
Le protocole et la banque restent compatibles avec 0.2.1/0.2.2. Tester Léovic
en restant connecté jusqu'à l'apparition des trois choix, poser la question
sur l'inscription, puis accepter ou refuser. Vérifier la transition textuelle
et le passage volontaire au clavier et à la souris. L'historique doit garder
la réplique initiale. Fournir les deux nouveaux `latest.log` si la progression
reste bloquée, en précisant le dernier texte ou bouton visible et le maintien
ou non de la connexion. Un personnage neuf n'est pas requis pour le test audio.

Résultats effectifs de compilation, JUnit et bancs natifs dans l'artefact
`indivis-voices-prototype` de la [PR #171](https://github.com/Arthuuuurrr/Indivis/pull/171/checks).
Aucune mesure FPS/MSPT ni recette complète des caméras n'est déduite de ces tests.
