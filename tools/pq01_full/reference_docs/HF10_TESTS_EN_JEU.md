# Rejouer PQ01 et effectuer la recette — HF10

## Relecture personnelle sans récompenses

Sur une copie du monde et avec le personnage voulu chargé, hors dialogue et hors transition :

```mcfunction
/function capitale:pq01/admin/test_debut
```

L’état antérieur de PQ01, ses drapeaux de dialogue, `QUEST_PROLOGUE`, `CAP_QUETEACTIVE` et la position sont sauvegardés une seule fois pour le couple compte Minecraft/personnage Nexus. Le personnage est replacé au début historique `764 299 1101` dans l’Overworld. La relecture ne réinitialise aucune autre quête, inventaire, réputation ou rang. Elle ne distribue aucune récompense, ne lance pas PQ02 et ne modifie pas le spawnpoint réel. Elle réutilise le TP historique d’amarrage `93 228 82`.

Après fermeture du dialogue et fin d’une éventuelle transition :

```mcfunction
/function capitale:pq01/admin/test_fin
```

Cette commande restaure uniquement la sauvegarde du personnage actif et sa position. Elle ne remet pas les inventaires ou le monde entier dans leur état précédent ; ne pas effectuer d’autres activités pendant une recette dont on souhaite retrouver exactement le contexte. Relancer `test_debut` en mode test ne remplace pas la sauvegarde initiale. Répéter l’opération séparément sur chaque personnage ayant un test ouvert.

## Parcours fonctionnel

1. Nouveau personnage/relecture : approcher Géraud à trois blocs après le délai de chargement et un déplacement ; le clic volontaire peut aussi ouvrir immédiatement l’introduction ; Gautier part pendant la réplique de Géraud, commence son interruption même s’il marche encore, puis s’arrête près du point prévu et reste pendant l’échange. Fermer puis rouvrir après chacune de ses deux répliques : les répliques déjà affichées ne doivent pas être répétées. Une reprise pendant la remontrance rejoint d’abord la position d’échange si nécessaire.
2. Après sa réponse, Gautier marche à son poste ; Géraud revient vers le joueur. Vérifier le menu répétable de Géraud et l’objectif vers Althéon.
3. Gautier, Hugues, Anselme, Perrin : chaque question et sous-question est cliquable ; questions disponibles directement sous la réponse et fermeture possible, sans fermer/reprendre entre les questions. Observer leurs occupations et leur suspension pendant leur dialogue. Aucun item n’est transféré par les gestes d’Hugues.
4. Althéon au clic droit, sans interpellation automatique : introduction une fois, amnésie canonique explicitement posée avant la conclusion. Toutes les familles A–E et sous-questions restent répétables. Avaleiv se déverrouille après la mission décrite par Althéon, indépendamment des discussions avec les matelots.
5. Une question facultative ne déclenche aucun TP. Seule la conclusion verte, explicitement marquée « Terminer l’entretien et lancer l’amarrage. », d’Althéon lance l’amarrage hors champ : sons/effets historiques, TP au navire amarré et objectif Géraud. Aucun nouveau dialogue de manœuvre.
6. Géraud au quai : cinq questions réutilisables ; seule la conclusion finale complète PQ01. Les autres membres d’équipage ont leurs réponses d’après-amarrage. Althéon garde la priorité MQ03 si elle est active.

En parcours normal, contrôler séparément sur un personnage de test approprié : Couronne +2, prime et compétences conservées, PQ01 terminé une seule fois, PQ02 disponible et objectif Léovic. Le mode de relecture supprime volontairement ces effets. Aucun rang 10 n’est donné par PQ01 ; au magistrat après Léovic, Visiteur 10 reste attribué. Un personnage de rang plus élevé ne doit pas être rétrogradé. Les fonctions du magistrat sont inchangées.

## Incidents, caméra et multijoueur

| Scénario à jouer | Résultat attendu |
| --- | --- |
| Joueurs A et B, déclenchements à dix secondes d’écart | Deux scènes personnelles ; le Gautier de B ne suit pas la progression de A ; aucun dialogue, choix, marqueur ou récompense croisé. |
| A traverse l’emplacement du Gautier de B | Aucun blocage/poussée par cet acteur invisible ; aucun effet sur une porte, une plaque ou la ronde de B. |
| Deux personnages du même compte | Reprises, drapeaux, récompenses et sauvegardes de relecture distincts. Changer pendant l’aller et pendant le retour. |
| Déconnexion pendant aller/remontrance/retour | Aucun acteur durable ou errant ; retour possible au stade de dialogue enregistré. |
| Fermer la caméra ou le menu pendant la marche | Navigation annulée et acteurs supprimés ; aucune fin de quête ou téléportation déclenchée par la fermeture. |
| Un autre dialogue remplace la caméra | Le nettoyage PQ01 ne ferme pas la nouvelle session appartenant à l’autre quête. |
| Porte fermée, escalier impraticable, chemin coupé | Arrêt/repli avec raison, sans déplacer l’original ni bloquer l’objectif Althéon. |
| Acteur supprimé/chunk déchargé | Annulation ou repli ; aucun clone abandonné. Après redémarrage, acteurs orphelins retirés à leur chargement. |
| Boutons anciens, doubles clics, durée audio active | Jeton/contexte contrôlé ; pas de double transition ni de double récompense. Le bouton fermer reste disponible en marche. |
| Éloignement, timeout, changement de dimension | Nettoyage sans progression involontaire ; reprise au bon stade. |
| Deux rondes de Perrin et échange Hugues/Gautier | Rondes indépendantes ; ambiance refusée près d’un autre joueur ; scène obligatoire prioritaire. |
| Navire amarré | Aucune destination du navire au large réutilisée au quai. Les points suffixés `_quai` sont indépendants. |

Chaque nouveau passage renouvelle le timeout de dialogue de 3 600 ticks par défaut, configurable dans `capitale:pq01_config timeout_ticks` entre 200 et 12 000. La distance maximale au point d’ouverture est de dix blocs. Les pages et blocs restent à l’écran jusqu’à l’action du joueur, et la voix en cours doit être terminée avant la suite. Les vraies réponses et les questions attendent leur sélection.

## Futurs doublages

Consulter [guide de doublage](https://github.com/Arthuuuurrr/Indivis/blob/feat/pq01-dirigeable-r2-20261022/docs/quests/pq01-dirigeable-r3/docs/DOUBLAGE_BLOCS.md) et [correspondance blocs/prises](https://github.com/Arthuuuurrr/Indivis/blob/feat/pq01-dirigeable-r2-20261022/docs/quests/pq01-dirigeable-r3/config/BLOCS_ET_PRISES.json). Les prises gardent leurs identifiants et sont jouées dans l’ordre du bloc, sans effacer le texte entre elles. Aucun audio nouveau n’est fourni.

## Retour arrière

Tant que R3 est présent, terminer chaque relecture avec `test_fin`, fermer les scènes et exécuter `nettoyer_scenes`. Si l’on souhaite aussi restaurer les hooks/noms ajoutés aux PNJ, libérer les liaisons individuelles avant désinstallation ; les positions d’édition ne sont pas restaurées automatiquement. Arrêter ensuite le serveur et remettre ensemble les deux datapacks et le journal précédents sur serveur/clients. Les stockages supplémentaires peuvent rester pour diagnostic ; aucun reset global Nexus n’est requis. Les récompenses normales déjà obtenues ne sont pas annulées par un retour de fichiers : pour annuler les effets de jeu, utiliser la sauvegarde cohérente monde/profils.

**Cette recette du modpack et de la carte reste à exécuter.** Un monde Fabric/EasyNPC isolé a validé la création/copie d’acteurs et quatre trajets réels ; il ne valide pas le rendu, le son ni les profils du monde utilisateur.

## Recette HF9 prioritaire

1. Ouvrir Géraud : il regarde le joueur pendant A1 et A2, puis Gautier pendant l’interruption, puis de nouveau le joueur. Attendre une minute sur A1 sans toucher les commandes : aucune avance automatique.
2. Lire le bloc « Matelot ! » suivi de la remontrance. Le texte reste jusqu’à l’action du joueur. Aucun personnage supplémentaire n’est ajouté au titre du dialogue.
3. Vérifier les blocs C1+C2, C3+C4 et C5+C6+questions du capitaine ; CD1+CD2, CE1+CE2 ; la conclusion CX1 à CX4 ; l’accueil et le départ de Géraud au quai.
4. Sur un bloc long, vérifier les pages : premier Espace/clic pour révéler si nécessaire, puis page suivante, puis bloc suivant à la dernière page. Refaire avec la frappe désactivée. Les questions ne se choisissent pas toutes seules.
5. Vérifier la conclusion verte du capitaine. Après sa sélection, lire l’intégralité de son bloc final : l’amarrage ne part qu’après la validation de fin, et après la voix si elle est configurée. Fermer ne déclenche pas l’amarrage.
6. Observer Perrin hors conversation durant au moins deux trajets et leurs pauses de six secondes. Si immobile, exécuter `/function capitale:pq01/admin/diagnostic_perrin`, puis conserver la ligne PERRIN_DIAG du log serveur. Aucun reset de PNJ.
7. Pour le doublage futur, configurer deux prises de test d’un même bloc. Vérifier ordre, durée, choix reçu pendant la lecture et annulation à la fermeture. Retirer ces sons de test ensuite. Le banc utilise des sons Minecraft uniquement pour vérifier l’ordonnancement ; aucune voix n’est incluse dans HF9.

Le mode de lecture actuel est celui de cette recette HF9 : pages et blocs manuels.

## Recette multijoueur HF10

1. Installer le journal HF10 sur le serveur et tous les clients, en conservant le core HF9. Les logs reçus montraient un client HF5 et un client HF9 ; cela ne prouve pas la cause des chutes de FPS.
2. Deux joueurs parlent à Géraud en même temps. Chacun voit son Gautier approcher et rester pendant son dialogue. Un observateur, puis un joueur qui arrive pendant la scène, voient les originaux et aucun double des deux scènes.
3. Fermer le dialogue du premier joueur : le second continue et ses acteurs restent présents. Le premier peut reprendre au clic droit.
4. Vérifier la marche aller/retour de Perrin avant toute progression puis après l’amarrage, avec deux joueurs proches. Le dialogue de Perrin suspend sa marche ; après fermeture, son activité reprend.
5. Changer de personnage et se déconnecter pendant une scène : les anciens acteurs disparaissent et les autres joueurs gardent leurs PNJ.
6. Rejouer les questions de Géraud et du capitaine, parcourir toutes les pages à son rythme, puis choisir explicitement la réponse verte.

Le banc valide le suivi réseau réel et la navigation. Le gain de FPS du modpack doit être mesuré en jeu : les logs reçus ne contiennent pas de profil de rendu. Les erreurs Nexus de NativeLayers23 et de transition du client HF5 sont distinctes et ne sont pas corrigées dans ce JAR.
