# Correctif SI1b — reconnaissance et liaison des gardes existants

## Cause confirmée sur le fichier réel

Le garde fourni contient une interaction `capitale:npc/gardecoeur/static_port/interact_tellraw`, un contrôle de proximité `capitale:access/static/archives_gardecoeur_from_npc`, et un événement `ON_OPEN_DIALOG` réutilisant `capitale:npc/gardecoeur/patrol/interact_tellraw`.

Le filtre SI1a assimilait toute occurrence du chemin `npc/gardecoeur/patrol/` à une patrouille mobile. Il rejetait donc ce garde statique des Archives malgré son contrôle d'accès valide. Le nouveau filtre distingue cette réutilisation de dialogue d'un circuit numéroté de patrouille ou d'un tag explicite de patrouille. Le test reproduit le refus sur le JAR SI1a, puis passe avec SI1b.

Les gardes des Archives sont présents dans le monde, comme précisé par Arthur. La réutilisation de ces entités est la procédure à suivre. Leurs fichiers complets ne sont pas publiés dans cette archive ou sur GitHub ; seuls les chemins de commandes utiles au test sont repris.

## Installer depuis SI1 ou SI1a

Serveur arrêté, remplacer :

- le journal par `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b.jar` (serveur et clients) ;
- le pack de quêtes par `hc_quetes_b22_V2_SEQUENCE_I_SI1b.zip` (datapacks du monde).

Retirer les anciennes variantes actives de ces deux fichiers. Le core et Nexus fournis dans le lot sont identiques aux versions SI1/SI1a, octet pour octet. Le mod admin est indépendant de ce correctif.

Aucun reset de quête, de score ou de personnage n'est nécessaire. Redémarrer pour charger le nouveau JAR.

## Lier les deux gardes déjà présents

Se placer à moins de trois blocs du premier garde des Archives, plus près de lui que de l'autre garde, puis exécuter :

```mcfunction
/function hc_quetes:liaison/sentinelle_archives_1
```

Se placer près du second garde, puis :

```mcfunction
/function hc_quetes:liaison/sentinelle_archives_2
```

La capture d'un modèle n'est pas une étape préalable à cette liaison. Si deux gardes sont à des distances trop proches (écart inférieur à 0,5 bloc), la commande refuse de choisir ; se rapprocher du garde voulu. Un même UUID ne peut occuper les deux postes. Un poste déjà lié à une autre entité est protégé. Répéter la liaison du même garde ne crée ni entité ni sauvegarde concurrente.

Les anciennes fonctions de pose des Sentinelles redirigent vers cette liaison lorsqu'un garde existant des Archives est repéré dans les huit blocs. Si la sélection du garde échoue, elles n'en créent pas un autre dans cette situation. L'enregistrement persistant des postes reste actif pour les PNJ créés auparavant.

## Modification limitée et sauvegarde

Avant toute modification, un instantané complet du garde et ses actions sont sauvegardés dans `hc_quetes:setup`, sous `existing_guard_1` ou `existing_guard_2`, avec UUID, dimension et liste des tags ajoutés.

La liaison accepte uniquement le modèle effectivement vérifié : interaction composée d'une seule commande `capitale:npc/gardecoeur/static_port/interact_tellraw`, exécutée comme joueur. Un autre modèle est refusé sans écrasement.

La commande d'interaction seule est remplacée par le routeur `hc_quetes:runtime/guard_interact`. Celui-ci donne la priorité judiciaire native dès `CAP_CRIME >= 10`, ouvre les étapes MQ concernées quand le personnage est admissible, puis utilise le dialogue statique d'origine en repli. Les autres événements sont conservés, dont proximité, ouverture de dialogue et les deux actions d'agression présentes dans le fichier fourni. Le niveau de permission d'origine reste conservé.

Les équipements, skin, nom, rotation, position, navigation et poste ne sont pas remplacés. Aucun ancrage de défense supplémentaire ni tag de garde statique du core n'est imposé à une entité existante. Les seuls tags ajoutés servent à la liaison et au journal. La cible du journal est enregistrée à la position réelle du garde, dans sa dimension.

## Restaurer avant retrait ou déplacement de la liaison

À moins de huit blocs du garde concerné, dans sa dimension :

```mcfunction
/function hc_quetes:liaison/liberer_sentinelle_archives_1
/function hc_quetes:liaison/liberer_sentinelle_archives_2
```

Exécuter chaque commande près de son garde. Elle restaure les événements d'origine et retire seulement les tags que la liaison avait ajoutés. Elle ne supprime pas le garde. Une modification cosmétique postérieure reste conservée.

Si les actions ont été modifiées après la liaison, la restauration refuse de les écraser et conserve la sauvegarde pour résolution manuelle. Le garde doit être chargé. Les anciennes fonctions `pose/liberer/sentinelle_1` et `sentinelle_2` redirigent également vers cette restauration pour les gardes existants.

Libérer les liaisons **avant** de retirer SI1b ou de déplacer volontairement un garde dans une autre dimension. Un retour à une sauvegarde complète du monde restaure aussi les données des entités, contrairement au seul remplacement des ZIP/JAR.

## Vérifications et limites

- Refus du filtre SI1a reproduit avec les chemins exacts d'événements du fichier NPC fourni ; acceptation corrigée et exclusion des patrouilles explicites vérifiées.
- Banc de liaison : sauvegarde antérieure à la modification, aucun spawn, deux gardes distincts, idempotence, préservation des événements/cosmétiques/permissions, restauration, conservation des autres liaisons, refus des actions modifiées ou d'une cible ambiguë.
- Contrôles statiques, migration, droits, dialogues, sessions, inventaire, installateur et Java réexécutés ; traces jointes.

**Pas de serveur Minecraft/Fabric exécuté ici.** La lecture du fichier réel et les bancs avec API doublées ne valident pas l'appel effectif des méthodes EasyNPC dans le jeu. La liaison, les conversations MQ, les contrôles d'accès multijoueurs et la restauration restent à vérifier sur une copie du serveur. Les erreurs de fonctions SI1/SI1a sont documentées ; aucune nouvelle validation en production n'est affirmée.
