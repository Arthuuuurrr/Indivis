# PQ01 — Préparation des doublages par bloc

24 octobre 2026. Le regroupement change l’affichage, sans changer les textes ni les 92 prises du dossier de direction d’acteur.

## Affichage et voix

Un bloc contient uniquement des répliques consécutives du même interlocuteur, jusqu’à une réponse du joueur, un changement de PNJ ou une transition. Exemples : A4+A5 pour Géraud ; C1+C2 pour l’accueil du capitaine ; C5+C6 puis les questions ; CX1 à CX4 pour sa conclusion. Les suffixes gardent leurs fonctions pour reprendre un ancien checkpoint sans imposer de réentendre le début du bloc.

`config/BLOCS_ET_PRISES.json` indique, pour chacune des 94 entrées ou variantes de reprise, les prises dans l’ordre, leur texte et leurs hooks. Les 95 identifiants du graphe historique restent stables ; C0 demeure un alias vers C1. Le dossier humain/Qwen livré auparavant garde donc ses 92 prises. Il n’est pas nécessaire de refaire tous les enregistrements en un fichier par bloc.

## Intégration audio ultérieure

1. Préparer une ressource par prise, avec le même timbre et la même prononciation par rôle. Garder les pauses de jeu voulues dans la prise ; retirer les grands blancs accidentels. Conserver notamment la continuité d’Althéon avec MQ03.
2. Convertir les prises destinées à Minecraft en OGG et les intégrer, avec leurs déclarations `sounds.json`, au futur JAR d’Indivis Voices prévu pour les clients. Cette étape d’intégration n’est pas effectuée dans HF9 et ne demande pas d’écraser les fichiers son existants.
3. Renseigner l’identifiant de son et la durée réelle de chaque prise dans `capitale:pq01_config voices.<prise>`. La durée est en ticks : arrondir au-dessus `secondes × 20`, en comptant les pauses conservées et la fin du mot.
4. Valider le bloc entier avec texte, sons et commandes réelles ; ajuster les durées à l’écoute. Ne pas ajouter un délai de lecture automatique pour faire disparaître le texte.

Exemple de syntaxe, à adapter aux ressources effectivement intégrées :

```mcfunction
/data modify storage capitale:pq01_config voices.a4 set value {sound:"indivis:voix/geraud/pq01_a4",ticks:24}
/data modify storage capitale:pq01_config voices.a5 set value {sound:"indivis:voix/geraud/pq01_a5",ticks:100}
```

Ces deux durées et noms de son sont des exemples ; ils ne sont pas des prises fournies et ne doivent pas être activés sans leurs ressources réelles.

## Fonctionnement déjà préparé dans HF9

Les prises configurées d’un bloc démarrent dans l’ordre. Une prise sans son est sautée sans attente fantôme. Une durée positive est respectée avant la suivante. Une prise configurée sans durée positive reçoit un tick minimal ; la durée réelle devra être renseignée pour le doublage définitif.

Les choix de quête sont mis en attente tant que la file du bloc est active. Une demande de suite conserve le texte affiché pendant la voix ; le lecteur garde ensuite la maîtrise de la lecture. Le bouton de fermeture et Échap arrêtent le son courant et annulent les prises restantes. Aucune validation différée ne doit déclencher une scène après la fermeture. La lecture réelle d’Indivis Voices, lorsqu’elle est détectée côté client, continue de bloquer la demande de suite jusqu’à sa fin.

Les tests automatiques vérifient deux sons Minecraft enchaînés, l’absence de nouvelle capture entre les prises, un choix reçu avant la fin, le saut de sons vides et l’annulation de la file. Ils ne constituent pas une écoute de voix doublées. Aucun audio de synthèse, aucun catalogue vocal et aucun nouveau JAR Indivis Voices ne sont produits dans HF9.
